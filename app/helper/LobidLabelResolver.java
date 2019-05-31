package helper;

import org.eclipse.rdf4j.rio.RDFFormat;

public class LobidLabelResolver {
    final public static String id = "http://lobid.org/resources";
    final public static String id2 = "https://lobid.org/resources";

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public static String lookup(String uri, String language) {
        try {
            return SparqlLookup.lookup(uri, uri, "http://purl.org/dc/terms/title", language, RDFFormat.JSONLD,
                    "application/json");
        } catch (Exception e) {
            try {
                return SparqlLookup.lookup(uri, uri + "#!", "http://purl.org/dc/terms/title", language,
                        RDFFormat.JSONLD, "application/json");
            } catch (Exception e2) {
                return uri;
            }
        }
    }
}
