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
            lResolver.put(FrlDummyLabelResolver.DOMAIN, new FrlDummyLabelResolver());
            lResolver.put(CCLabelResolver.DOMAIN, new CCLabelResolver());
            lResolver.put(SchemaDummyLabelResolver.DOMAIN, new SchemaDummyLabelResolver());
            lResolver.put(GithubDummyLabelResolver.DOMAIN, new GithubDummyLabelResolver());
            lResolver.put(GitUserDummyLabelResolver.DOMAIN, new GitUserDummyLabelResolver());
            lResolver.put(LocDummyLabelResolver.DOMAIN, new LocDummyLabelResolver());
            lResolver.put(OldDataHubDummyLabelResolver.DOMAIN, new OldDataHubDummyLabelResolver());
            lResolver.put(WorldcatDummyLabelResolver.DOMAIN, new WorldcatDummyLabelResolver());
            lResolver.put(PurlLabelResolver.DOMAIN, new PurlLabelResolver());
            lResolver.put(SkosLabelResolver.DOMAIN, new SkosLabelResolver());
            lResolver.put(EtikettMaker.TOSCIENCE_API_URL, new ToscienceApiLabelResolver());
            lResolver.put(ResearchOrganizationLabelResolver.DOMAIN, new ResearchOrganizationLabelResolver());

            return lResolver;
        }

        private static LabelResolver getLabelResolver(String domain) {
            play.Logger.debug("Method getLabelResolver Domain : " + domain);
            Hashtable<String, LabelResolver> lResolvTable = null;
            lResolvTable = getLabelResolverTable();
            LabelResolver labelResolver = lResolvTable.get(domain);
            play.Logger.debug("Return LabelResolver of Class: " + labelResolver.getClass().toString());
            return labelResolver;
        }

    }

}
