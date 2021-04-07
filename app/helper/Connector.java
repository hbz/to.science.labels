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
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author aquast Helper Class to generate a URLConnection via http or https and
 *         circumvent the change protocol issue of HttpURLConnector see:
 *         https://stackoverflow.com/questions/1884230
 *
 */
public class Connector {

    private static URLConnection urlConn = null;
    private URL url = null;
    private static final int HTTP = 0;
    private static final int HTTPS = 1;
    private static int protocol = -1;
    private static int httpStatus = -1;
    private static String redirectLocation = null;
    private static Properties reqProp = new Properties();
    private static InputStream inStream = null;

    private Connector() {

    }

    public static String getRedirectLocation() {
        return redirectLocation;
    }

    /**
     * Initiate URLConnection as https connection if protocol in URL is https
     * 
     * @param url
     * @return HttpURLConnection
     */
    private static HttpURLConnection getHttpConn(URL url) {
        HttpURLConnection httpConn = null;
        try {
            httpConn = (HttpURLConnection) url.openConnection();
            addProperties(httpConn);
            httpStatus = httpConn.getResponseCode();
            redirectLocation = httpConn.getHeaderField("Location");
            inStream = httpConn.getInputStream();
            play.Logger.debug("http Status Code: " + httpStatus + "Location Header: " + redirectLocation);
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
    private static HttpsURLConnection getHttpsConn(URL url) {
        HttpsURLConnection httpsConn = null;
        try {
            httpsConn = (HttpsURLConnection) url.openConnection();
            addProperties(httpsConn);
            httpStatus = httpsConn.getResponseCode();
            redirectLocation = httpsConn.getHeaderField("Location");
            inStream = httpsConn.getInputStream();
            play.Logger.debug("http Status Code: " + httpStatus + "Location Header: " + redirectLocation);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return httpsConn;
    }

    private static void addProperties(URLConnection connect) {
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
    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Checks if http Status determines redirect and if redirect location is
     * associated with protocol change from http to https. If both is true,
     * initiate new HttpsURLConnection
     * 
     */
    private static void performProtocolChange() {

        if ((299 < getStatusCode() && getStatusCode() < 400) && getRedirectLocation().startsWith("https")) {
            play.Logger.debug("found https-protocol and redirect-location: " + getRedirectLocation());
            urlConn = getHttpsConn(createUrl(getRedirectLocation()));
        }

    }

    public URLConnection getConnection() {
        return urlConn;
    }

    public static int getStatusCode() {
        return httpStatus;
    }

    public InputStream getInputStream() {
        return inStream;
    }

    public void setConnectorProperty(String key, String value) {
        reqProp.put(key, value);
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
        performProtocolChange();
    }

    public static class Factory {

        private static Connector conn = new Connector();

        /**
         * Provide Connector Instance that automatically returns the appropriate
         * URLConnector. This is either HttpURLConnector or HttpsURLConnector.
         * 
         * @param url
         * @return
         */
        public static Connector getInstance(URL url) {

            if (url.getProtocol().equals("http")) {
                protocol = HTTP;
            } else {
                protocol = HTTPS;
            }
            // performProtocolChange(conn);
            conn.url = url;
            return conn;
        }
    }

}
