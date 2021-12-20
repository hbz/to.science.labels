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
            URL url = createUrlFromString(urlString);
            play.Logger.debug("Domain extracted from urlString: " + url.getHost());
            LabelResolver lResolv = getLabelResolver(url.getHost());
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
            lResolver.put(LicenseLabelResolver.DOMAIN, new LicenseLabelResolver());
            lResolver.put(LanguageLabelResolver.DOMAIN, new LanguageLabelResolver());
            lResolver.put(OrcaMediaTypesLabelResolver.DOMAIN, new OrcaMediaTypesLabelResolver());
            lResolver.put(ResearchOrganizationLabelResolver.DOMAIN, new ResearchOrganizationLabelResolver());
            return lResolver;
        }

        private static LabelResolver getLabelResolver(String domain) {
            // play.Logger.debug("Method getLabelResolver Domain : " + domain);
            LabelResolver labelResolver = getLabelResolverTable().get(domain);
            play.Logger.debug("Return LabelResolver of Class: " + labelResolver.getClass().toString());
            return labelResolver;
        }

    }

}
