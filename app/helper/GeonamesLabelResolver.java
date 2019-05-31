package helper;

import org.eclipse.rdf4j.rio.RDFFormat;

public class GeonamesLabelResolver {

    final public static String id = "http://www.geonames.org/";
    final public static String id2 = "https://www.geonames.org/";

    public static String lookup(String uri, String language) {
        try {
            return SparqlLookup.lookup(uri + "/about.rdf", "?s", "http://www.geonames.org/ontology#name", language,
                    RDFFormat.RDFXML, "application/rdf+xml");
        } catch (Exception e) {
            return uri;
        }
    }

}
