package helper;

import java.net.URL;
import java.text.Normalizer;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;

public class LobidLabelResolver {
    final public static String id = "http://lobid.org/resources";

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public static String lookup(String uri) {
        try {
            for (Statement s : RdfUtils.readRdfToGraph(new URL(uri), RDFFormat.JSONLD, "application/json")) {
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
            play.Logger.warn("Not able to include data from" + uri);
        }
        return null;
    }

    private static String findLabel(Statement s, String uri) {
        if (!uri.equals(s.getSubject().stringValue()))
            return null;
        if ("http://purl.org/dc/terms/title".equals(s.getPredicate().stringValue())) {
            return s.getObject().stringValue();
        }
        return null;
    }
}
