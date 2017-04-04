/*Copyright (c) 2015 "hbz"

This file is part of etikett.

etikett is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package helper;

import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * @author Jan Schnasse
 *
 */
@SuppressWarnings("javadoc")
public class DefaultLabelResolver {
    public final static String prefLabel = "http://www.w3.org/2004/02/skos/core#prefLabel";
    public final static String title = "http://purl.org/dc/terms/title";
    final public static String id = "";

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public static String lookup(String uri, String language) {
        try {
            return lookup(uri, language, RDFFormat.RDFXML, "application/rdf+xml");
        } catch (Exception e) {
            try {
                return lookup(uri, language, RDFFormat.NTRIPLES, "text/plain");
            } catch (Exception e2) {
                return lookup(uri, language, RDFFormat.JSONLD, "application/json");
            }
        }
    }

    private static String lookup(String uri, String language, RDFFormat format, String accept) {
        List<String> collectLabels = new ArrayList<>();
        try {
            Collection<Statement> statements = RdfUtils.readRdfToGraph(new URL(uri), format, accept);
            List<Statement> prefLabels = statements.stream().filter((s) -> {
                boolean isLabel = prefLabel.equals(s.getPredicate().stringValue());
                boolean isSubjectOfInterest = uri.equals(s.getSubject().stringValue());
                return isLabel && isSubjectOfInterest;
            }).collect(Collectors.toList());
            for (Statement s : prefLabels) {
                if (s.getObject() instanceof Literal) {
                    Literal literal = normalizeLiteral((Literal) s.getObject());
                    collectLabels.add(literal.stringValue());
                    String label = getLabelInLanguage(literal, language);
                    if (label != null)
                        return label;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return collectLabels.toString();
    }

    private static Literal normalizeLiteral(Literal l) {
        ValueFactory v = SimpleValueFactory.getInstance();
        Literal newLiteral;
        if (l.getLanguage().isPresent()) {
            String l_lang = l.getLanguage().get();
            newLiteral = v.createLiteral(Normalizer.normalize(l.stringValue(), Normalizer.Form.NFKC), l_lang);
        } else {
            newLiteral = v.createLiteral(Normalizer.normalize(l.stringValue(), Normalizer.Form.NFKC));
        }
        return newLiteral;
    }

    static String getLabelInLanguage(Literal rdfOL, String language) {
        String l = rdfOL.getLanguage().get();
        if (language.equals(l)) {
            return rdfOL.stringValue();
        }
        return null;
    }
}
