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

        private static Hashtable<String, LabelResolver> lResolver = new Hashtable<String, LabelResolver>();

        public static LabelResolver getInstance(String urlString) {
            LabelResolver lResolv = null;
            URL url = createUrlFromString(urlString);
            play.Logger.debug("Domain extracted from urlString: " + url.getHost());
            lResolv = getLabelResolver(url.getHost());

            return lResolv;
        }

        public static boolean existsLabelResolver(String urlString) {
            URL url = createUrlFromString(urlString);
            return lResolver.containsKey(url.getHost());
        }

        private static URL createUrlFromString(String urlString) {
            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return url;
        }

        private static LabelResolver getLabelResolver(String domain) {

            // put all known Class that implements the Interface into Hashtable
            lResolver.put(CrossrefLabelResolver.DOMAIN, new CrossrefLabelResolver());
            lResolver.put(OrcidLabelResolver.DOMAIN, new OrcidLabelResolver());
            lResolver.put(GeonamesLabelResolver.DOMAIN, new GeonamesLabelResolver());
            lResolver.put(GndLabelResolver.DOMAIN, new GndLabelResolver());
            lResolver.put(OpenStreetMapLabelResolver.DOMAIN, new OpenStreetMapLabelResolver());
            lResolver.put(LobidLabelResolver.DOMAIN, new LobidLabelResolver());

            // return LabelResolver in accordance to the Domain requested
            return lResolver.get(domain);
        }

    }

}
