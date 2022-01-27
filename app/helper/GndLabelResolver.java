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
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;

import models.Etikett;

/**
 * @author Jan Schnasse
 *
 */
@SuppressWarnings("javadoc")
public class GndLabelResolver extends LabelResolverService implements LabelResolver {

    public GndLabelResolver() {
        super();
    }

    public final static String DOMAIN = "d-nb.info";

    final public static String protocol = "https://";
    final public static String alternateProtocol = "http://";
    final public static String namespace = "d-nb.info/standards/elementset/gnd#";

    final public static String id = alternateProtocol + "d-nb.info/gnd/";
    final public static String id2 = protocol + "d-nb.info/gnd/";

    public static Properties turtleObjectProp = new Properties();

    private static void setProperties() {
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

    protected void lookupAsync(String uri, String language) {
        try {
            play.Logger.info("Lookup Label from GND. Language selection is not supported yet! " + uri);

            // Workaround for d-nb: change protocol to https
            String sslUrl = uri.replace("http://", "https://");
            URL dnbUrl = new URL(sslUrl + "/about/lds");
            Collection<Statement> statement = new RdfUtils().readRdfToGraph(dnbUrl, RDFFormat.RDFXML,
                    "application/rdf+xml");

            Iterator<Statement> sit = statement.iterator();

            while (sit.hasNext()) {
                Statement partialStatement = sit.next();
                boolean isLiteral = partialStatement.getObject() instanceof Literal;
                if (!(partialStatement.getSubject() instanceof BNode)) {
                    if (isLiteral) {
                        ValueFactory v = SimpleValueFactory.getInstance();
                        Statement newS = v.createStatement(partialStatement.getSubject(),
                                partialStatement.getPredicate(), v.createLiteral(Normalizer
                                        .normalize(partialStatement.getObject().stringValue(), Normalizer.Form.NFKC)));
                        String tmpLabel = findLabel(newS, uri);
                        if (tmpLabel != null) {
                            play.Logger.info("Found Label: " + tmpLabel);
                            label = tmpLabel;
                            etikett.setLabel(label);
                            cacheEtikett(etikett);
                        } else {
                            tmpLabel = findLabel(newS, sslUrl);
                            if (tmpLabel != null) {
                                play.Logger.info("Found Label with https: " + tmpLabel);
                                label = tmpLabel;
                                etikett.setLabel(label);
                                cacheEtikett(etikett);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            play.Logger.error("Getting Exception from RdfUtils, failed to find label for " + uri);
        }
    }

    private String findLabel(Statement stmnt, String uri) {
        if (!uri.equals(stmnt.getSubject().stringValue())) {
            return null;
        }
        GndLabelResolver.setProperties();

        Enumeration<Object> keys = turtleObjectProp.keys();
        while (keys.hasMoreElements()) {
            String predicateProperty = turtleObjectProp.getProperty((String) keys.nextElement());
            String predicate = protocol + namespace + predicateProperty;
            if (predicate.equals(stmnt.getPredicate().stringValue())) {
                return refineLabel(predicateProperty, stmnt.getObject().stringValue());
                // return stmnt.getObject().stringValue();
            }
        }

        keys = turtleObjectProp.keys();
        while (keys.hasMoreElements()) {
            String predicateProperty = turtleObjectProp.getProperty((String) keys.nextElement());
            String predicate = alternateProtocol + namespace + predicateProperty;
            if (predicate.equals(stmnt.getPredicate().stringValue())) {
                return refineLabel(predicateProperty, stmnt.getObject().stringValue());
                // return stmnt.getObject().stringValue();
            }
        }
        return null;
    }

    private String refineLabel(String predProp, String stmntValue) {
        if (predProp.equals("preferredNameForThePerson")) {
            // refine for Name-Sequence
            String[] personNames = stmntValue.split(", ");
            StringBuffer personNameLabel = new StringBuffer();
            for (int i = personNames.length; i > 0; i--) {
                personNameLabel.append(personNames[i - 1] + " ");
            }
            return personNameLabel.toString();
        }
        return stmntValue;
    }

}
