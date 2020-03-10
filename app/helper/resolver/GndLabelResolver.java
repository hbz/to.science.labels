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
package helper.resolver;

import java.net.URL;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * @author Jan Schnasse
 * @author Andres Quast quast@hbz-nrw.de
 *
 */
public class GndLabelResolver extends LabelResolver implements LabelResolverInterface {

    final public static String protocol = "https://";
    final public static String alternateProtocol = "http://";
    final public static String namespace = "d-nb.info/standards/elementset/gnd#";
    final public static String domain = "d-nb.info";

    final public static String id = alternateProtocol + domain;
    final public static String id2 = protocol + domain;

    public static Properties turtleObjectProp = new Properties();

    public GndLabelResolver() {
        setProperties();
    }

    private void setProperties() {
        turtleObjectProp.setProperty("namespace", "d-nb.info/standards/elementset/gnd#");
        turtleObjectProp.setProperty("preferredName", "preferredName");
        turtleObjectProp.setProperty("preferredNameForTheConferenceOrEvent", "preferredNameForTheConferenceOrEvent");
        turtleObjectProp.setProperty("preferredNameForTheCorporateBody", "preferredNameForTheCorporateBody");
        turtleObjectProp.setProperty("preferredNameForThePerson", "preferredNameForThePerson");
        turtleObjectProp.setProperty("preferredNameForThePlaceOrGeographicName",
                "preferredNameForThePlaceOrGeographicName");
        turtleObjectProp.setProperty("preferredNameForTheSubjectHeading", "preferredNameForTheSubjectHeading");
        turtleObjectProp.setProperty("preferredNameForTheWork", "preferredNameForTheWork");

    }

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public static String lookup(String uri, String language) {
        try {
            play.Logger.info("Lookup Label from GND. Language selection is not supported yet! " + uri);

            Collection<Statement> statement = RdfUtils.readRdfToGraph(new URL(uri + "/about/lds"), RDFFormat.RDFXML,
                    "application/rdf+xml");

            Iterator<Statement> sit = statement.iterator();

            while (sit.hasNext()) {
                Statement s = sit.next();
                boolean isLiteral = s.getObject() instanceof Literal;
                if (!(s.getSubject() instanceof BNode)) {
                    if (isLiteral) {
                        ValueFactory v = SimpleValueFactory.getInstance();
                        Statement newS = v.createStatement(s.getSubject(), s.getPredicate(), v.createLiteral(
                                Normalizer.normalize(s.getObject().stringValue(), Normalizer.Form.NFKC)));
                        String label = findLabel(newS, uri);
                        if (label != null) {
                            play.Logger.info("Found Label: " + label);
                            return label;
                        }
                    }
                }
            }

        } catch (Exception e) {
            play.Logger.error("Failed to find label for " + uri, e);
        }
        return null;
    }

    private static String findLabel(Statement s, String uri) {
        if (!uri.equals(s.getSubject().stringValue())) {
            return null;
        }
        Enumeration<Object> keys = turtleObjectProp.keys();
        while (keys.hasMoreElements()) {
            String predicate = protocol + namespace + turtleObjectProp.getProperty((String) keys.nextElement());
            if (predicate.equals(s.getPredicate().stringValue())) {
                return s.getObject().stringValue();
            }
        }

        keys = turtleObjectProp.keys();
        while (keys.hasMoreElements()) {
            String predicate = alternateProtocol + namespace
                    + turtleObjectProp.getProperty((String) keys.nextElement());
            if (predicate.equals(s.getPredicate().stringValue())) {
                return s.getObject().stringValue();
            }
        }
        return null;
    }

    public String getResolverDomain() {
        return domain;
    }

    public String getLabelResolverClassName() {
        return GndLabelResolver.class.getCanonicalName();
    }

}
