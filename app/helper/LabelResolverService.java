package helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import models.Etikett;

/**
 * @author aquast LabelResolverService provides the default methods required for
 *         the specific Authority Files LabelResolvers. LabelResolverService can
 *         be extended by specific LabelResolver Classes that implement the
 *         LabelResolver Interface
 *
 */
public abstract class LabelResolverService implements Runnable {

    public String urlString = null;
    public String label = null;
    public String language = null;
    public Etikett etikett = null;

    /**
     * Default lookup Method that returns a label for an given Uri-String.
     * Either from Etikett Cache or from requesting the Url
     * 
     * @param uri
     *            the URI-String or URI-String we like to have resolved as label
     * @param language
     *            not supported
     * @return etikettLabel The label requested
     */
    public String lookup(String uri, String language) {
        this.urlString = uri;
        this.language = language;
        String etikettLabel = null;
        this.etikett = getEtikett(uri);
        if (etikett != null) {
            etikettLabel = etikett.getLabel();
            runLookupThread();
        } else {
            etikett = new Etikett(urlString);
            lookupAsync(urlString, language);
            etikettLabel = label;
        }
        return etikettLabel;
    }

    protected abstract void lookupAsync(String uri, String language);

    protected InputStream urlToInputStream(URL url, Map<String, String> args) {

        Connector hConn = Connector.Factory.getInstance(url);
        if (args != null) {
            for (Entry<String, String> e : args.entrySet()) {
                hConn.setConnectorProperty(e.getKey(), e.getValue());
            }
        }
        hConn.connect();
        return hConn.getInputStream();
    }

    @Override
    public void run() {

        lookupAsync(urlString, language);

    }

    protected void runLookupThread() {

        Thread thread = new Thread(this);
        thread.start();
    }

    protected Etikett getEtikett(String urlString) {
        EtikettMaker eMaker = new EtikettMaker();
        return eMaker.getValue(urlString);
    }

    protected void cacheEtikett(Etikett etikett) {
        EtikettMaker eMaker = new EtikettMaker();
        eMaker.addJsonDataIntoDBCache(etikett);
    }

}
