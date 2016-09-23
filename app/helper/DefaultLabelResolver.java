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

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;

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
            for (Statement s : RdfUtils.readRdfToGraph(new URL(uri), RDFFormat.NTRIPLES, "text/plain")) {
                boolean isLiteral = s.getObject() instanceof Literal;
                if (!(s.getSubject() instanceof BNode)) {
                    if (isLiteral) {
                        ValueFactory v = new ValueFactoryImpl();
                        Statement newS = v.createStatement(s.getSubject(), s.getPredicate(), v.createLiteral(
                                Normalizer.normalize(s.getObject().stringValue(), Normalizer.Form.NFKC)));
                        String label = findLabel(newS, uri, language);
                        if (label != null)
                            return label;
                    }
                }
            }
        } catch (Exception e) {
            play.Logger.warn("Not able to include data from" + uri, e);
        }
        return null;
    }

    static String findLabel(Statement s, String uri, String language) {
        if (!uri.equals(s.getSubject().stringValue()))
            return null;
        if (prefLabel.equals(s.getPredicate().stringValue())) {
            Value rdfO = s.getObject();
            if (rdfO instanceof Literal) {
                Literal rdfOL = (Literal) rdfO;
                play.Logger.debug(
                        "Found " + rdfOL.getLanguage() + " label for " + uri + " : " + s.getObject().stringValue());
                if (language.equals(rdfOL.getLanguage())) {
                    return s.getObject().stringValue();
                } else if ("de".equals(rdfOL.getLanguage())) {
                    return s.getObject().stringValue();
                } else if ("en".equals(rdfOL.getLanguage())) {
                    return s.getObject().stringValue();
                }
            }

        }
        if (title.equals(s.getPredicate().stringValue())) {
            return s.getObject().stringValue();
        }
        return null;
    }
}
