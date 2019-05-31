package helper;

import java.net.URL;
import java.text.Normalizer;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;

public class GeonamesLabelResolver {

    private static Object namespace = "http://www.geonames.org/ontology#";
    private static Object geographicName = namespace + "name";

    final public static String id = "http://www.geonames.org/";
    final public static String id2 = "https://www.geonames.org/";

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public static String lookup(String uri) {
        try {
            for (Statement s : RdfUtils.readRdfToGraph(new URL(uri + "/about.rdf"), RDFFormat.RDFXML,
                    "application/rdf+xml")) {
                boolean isLiteral = s.getObject() instanceof Literal;
                if (!(s.getSubject() instanceof BNode)) {
                    if (isLiteral) {
                        ValueFactory v = SimpleValueFactory.getInstance();
                        Statement newS = v.createStatement(s.getSubject(), s.getPredicate(), v.createLiteral(
                                Normalizer.normalize(s.getObject().stringValue(), Normalizer.Form.NFKC)));
                        String label = findLabel(newS, uri);
                        if (label != null)
                            return label;
                    }
                }
            }
        } catch (Exception e) {
            play.Logger.debug("Failed to find label for " + uri, e);
        }
        return null;
    }

    private static String findLabel(Statement s, String uri) {

        play.Logger.debug("Compare statement " + s.getPredicate().stringValue() + " with " + geographicName);

        if (geographicName.equals(s.getPredicate().stringValue())) {
            play.Logger.debug("Found " + s.getObject().stringValue());
            return s.getObject().stringValue();
        }

        return null;
    }
}
