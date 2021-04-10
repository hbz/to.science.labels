/**
 * 
 */
package helper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

/**
 * @author aquast
 *
 */
public interface LabelResolver {

    public String lookup(String urlString, String Language);

    public static class Factory {

        public static LabelResolver getInstance(String urlString) {
            LabelResolver lResolv = null;
            URL url = createUrlFromString(urlString);
            play.Logger.debug("Domain extracted from urlString: " + url.getHost());
            lResolv = getLabelResolver(url.getHost());

            return lResolv;
        }

        public boolean existsLabelResolver(String urlString) {
            URL url = createUrlFromString(urlString);
            return getLabelResolverTable().containsKey(url.getHost());
        }

        private static URL createUrlFromString(String urlString) {
            URL createdUrl = null;
            try {
                createdUrl = new URL(urlString);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                play.Logger.warn("Can't generate URL from " + urlString);
            }
            return createdUrl;
        }

        private static Hashtable<String, LabelResolver> getLabelResolverTable() {

            Hashtable<String, LabelResolver> lResolver = new Hashtable<String, LabelResolver>();
            // put all known Class that implements the Interface into Hashtable
            lResolver.put(CrossrefLabelResolver.DOMAIN, new CrossrefLabelResolver());
            lResolver.put(OrcidLabelResolver.DOMAIN, new OrcidLabelResolver());
            lResolver.put(GeonamesLabelResolver.DOMAIN, new GeonamesLabelResolver());
            lResolver.put(GndLabelResolver.DOMAIN, new GndLabelResolver());
            lResolver.put(OpenStreetMapLabelResolver.DOMAIN, new OpenStreetMapLabelResolver());
            lResolver.put(LobidLabelResolver.DOMAIN, new LobidLabelResolver());
            lResolver.put(DummyLabelResolver.DOMAIN, new DummyLabelResolver());
            return lResolver;
        }

        private static LabelResolver getLabelResolver(String domain) {
            Hashtable<String, LabelResolver> lResolvTable = null;
            lResolvTable = getLabelResolverTable();
            return lResolvTable.get(domain);
        }

    }

}
