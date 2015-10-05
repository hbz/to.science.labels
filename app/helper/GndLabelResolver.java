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
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFFormat;

/**
 * @author Jan Schnasse
 *
 */
@SuppressWarnings("javadoc")
public class GndLabelResolver {

    final public static String namespace = "http://d-nb.info/standards/elementset/gnd#";

    final public static String preferredName = namespace + "preferredName";
    final public static String preferredNameForTheConferenceOrEvent = namespace
	    + "preferredNameForTheConferenceOrEvent";
    final public static String preferredNameForTheCorporateBody = namespace
	    + "preferredNameForTheCorporateBody";
    final public static String preferredNameForThePerson = namespace
	    + "preferredNameForThePerson";
    final public static String preferredNameForThePlaceOrGeographicName = namespace
	    + "preferredNameForThePlaceOrGeographicName";
    final public static String preferredNameForTheSubjectHeading = namespace
	    + "preferredNameForTheSubjectHeading";
    final public static String preferredNameForTheWork = namespace
	    + "preferredNameForTheWork";

    final public static String id = "http://d-nb.info/gnd/";

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public static String lookup(String uri) {
	try {
	    for (Statement s : RdfUtils.readRdfToGraph(new URL(uri
		    + "/about/lds"), RDFFormat.RDFXML, "application/rdf+xml")) {
		boolean isLiteral = s.getObject() instanceof Literal;
		if (!(s.getSubject() instanceof BNode)) {
		    if (isLiteral) {
			ValueFactory v = new ValueFactoryImpl();
			Statement newS = v.createStatement(s.getSubject(), s
				.getPredicate(), v.createLiteral(Normalizer
				.normalize(s.getObject().stringValue(),
					Normalizer.Form.NFKC)));
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
	if (preferredName.equals(s.getPredicate().stringValue())) {
	    return s.getObject().stringValue();
	}
	if (preferredNameForThePerson.equals(s.getPredicate().stringValue())) {
	    return s.getObject().stringValue();
	}
	if (preferredNameForTheConferenceOrEvent.equals(s.getPredicate()
		.stringValue())) {
	    return s.getObject().stringValue();
	}
	if (preferredNameForTheCorporateBody.equals(s.getPredicate()
		.stringValue())) {
	    return s.getObject().stringValue();
	}
	if (preferredNameForThePerson.equals(s.getPredicate().stringValue())) {
	    return s.getObject().stringValue();
	}
	if (preferredNameForThePlaceOrGeographicName.equals(s.getPredicate()
		.stringValue())) {
	    return s.getObject().stringValue();
	}
	if (preferredNameForTheSubjectHeading.equals(s.getPredicate()
		.stringValue())) {
	    return s.getObject().stringValue();
	}
	if (preferredNameForTheWork.equals(s.getPredicate().stringValue())) {
	    return s.getObject().stringValue();
	}
	return null;
    }
}
