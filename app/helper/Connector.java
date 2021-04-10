/**
 * 
 */
package helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author aquast Helper Class to generate a URLConnection via http or https and
 *         circumvent the change protocol issue of HttpURLConnector see:
 *         https://stackoverflow.com/questions/1884230
 *
 */
public class Connector {

    private static final int HTTP = 0;
    private static final int HTTPS = 1;

    private URLConnection urlConn = null;
    private URL url = null;
    private int protocol = -1;
    private int httpStatus = -1;
    private String redirectLocation = null;
    private Properties reqProp = new Properties();
    private InputStream inStream = null;
    private String typeAccepted = null;
    private String contentType = null;

    private Connector() {

    }

    public String getRedirectLocation() {
        return this.redirectLocation;
    }

    /**
     * Initiate URLConnection as https connection if protocol in URL is https
     * 
     * @param url
     * @return HttpURLConnection
     */
    private HttpURLConnection getHttpConn(URL url) {
        HttpURLConnection httpConn = null;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
            addProperties(httpConn);
            httpStatus = httpConn.getResponseCode();
            this.redirectLocation = httpConn.getHeaderField("Location");
            this.inStream = httpConn.getInputStream();
            this.typeAccepted = httpConn.getRequestProperty("Accept");
            this.contentType = httpConn.getContentType();
            play.Logger.debug("http Status Code: " + httpStatus + ", Location Header: " + redirectLocation
                    + "\n Connection : " + httpConn.toString() + "\n ContentType der Response: "
                    + httpConn.getContentType() + "\n Accept-Header " + httpConn.getRequestProperty("Accept"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return httpConn;
    }

    /**
     * Initiate URLConnection as https connection if protocol in URL is https
     * 
     * @param url
     * @return HttpsURLConnection
     */
    private HttpsURLConnection getHttpsConn(URL url) {
        HttpsURLConnection httpsConn = null;
        try {
            httpsConn = (HttpsURLConnection) url.openConnection();
            addProperties(httpsConn);
            this.httpStatus = httpsConn.getResponseCode();
            this.redirectLocation = httpsConn.getHeaderField("Location");
            this.inStream = httpsConn.getInputStream();
            this.typeAccepted = httpsConn.getRequestProperty("Accept");
            this.contentType = httpsConn.getContentType();
            play.Logger.debug("http Status Code: " + httpStatus + ", Location Header: " + redirectLocation
                    + "\n Connection : " + httpsConn.toString() + "\n ContentType der Response: "
                    + httpsConn.getContentType() + "\n Accept-Header " + httpsConn.getRequestProperty("Accept"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return httpsConn;
    }

    private void addProperties(URLConnection connect) {
        Enumeration<Object> propEnum = reqProp.keys();
        while (propEnum.hasMoreElements()) {
            String key = propEnum.nextElement().toString();
            connect.setRequestProperty((String) key, (String) reqProp.get(key));
        }
    };

    /**
     * Generates and returns URL Instance from String
     * 
     * @param urlString
     * @return
     */
    private URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            play.Logger.warn("cannot create URL Instance from " + urlString);
        }
        return url;
    }

    /**
     * Checks if http Status determines redirect and if redirect location is
     * associated with protocol change from http to https. If both is true,
     * initiate new HttpsURLConnection
     * 
     */
    private void performProtocolChangeToHttps() {

        if ((299 < getStatusCode() && getStatusCode() < 400) && getRedirectLocation().startsWith("https")) {
            play.Logger.debug("found https-protocol and redirect-location: " + this.getRedirectLocation());
            urlConn = this.getHttpsConn(createUrl(getRedirectLocation()));
            performProtocolChangeToHttp();
        }

    }

    /**
     * Checks if http Status determines redirect and if redirect location is
     * associated with protocol change from https to http (e.g CrossrefFunder).
     * If both is true, initiate new HttpURLConnection
     * 
     */
    private void performProtocolChangeToHttp() {

        if ((299 < getStatusCode() && getStatusCode() < 400) && getRedirectLocation().startsWith("http")) {
            play.Logger.debug("found http-protocol and redirect-location: " + this.getRedirectLocation());
            urlConn = this.getHttpConn(createUrl(getRedirectLocation()));
            performProtocolChangeToHttps();
        }

    }

    public URLConnection getConnection() {
        return urlConn;
    }

    public int getStatusCode() {
        return httpStatus;
    }

    public InputStream getInputStream() {
        return inStream;
    }

    public void setConnectorProperty(String key, String value) {
        this.reqProp.put(key, value);
    }

    public String getTypeAccepted() {
        return typeAccepted;
    }

    public String getContentType() {
        return contentType;
    }

    public void connect() {
        switch (protocol) {
        case 0:
            urlConn = getHttpConn(url);
            break;
        case 1:
            urlConn = getHttpsConn(url);
            break;
        }
        performProtocolChangeToHttps();
        performProtocolChangeToHttp();
    }

    public static class Factory {

        /**
         * Provide Connector Instance that automatically returns the appropriate
         * URLConnector. This is either HttpURLConnector or HttpsURLConnector.
         * 
         * @param url
         * @return
         */
        public static Connector getInstance(URL url) {

            Connector conn = new Connector();

            if (url.getProtocol().equals("http")) {
                conn.protocol = Connector.HTTP;
            } else {
                conn.protocol = Connector.HTTPS;
            }
            conn.url = url;
            return conn;
        }
    }

}
