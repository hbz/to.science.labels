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

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import models.Etikett;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

import com.avaje.ebean.Ebean;

import play.Play;

/**
 * @author Jan Schnasse
 *
 */
public class ApplicationProfile {

    /**
     * prefLabel predicate will be analysed
     */
    public final static String prefLabel = "http://www.w3.org/2004/02/skos/core#prefLabel";

    /**
     * icon predicate will be analyzed
     */
    public final static String icon = "http://www.w3.org/1999/xhtml/vocab#icon";

    /**
     * name predicate will be analyzed
     */
    public final static String name = "http://hbz-nrw.de/regal#jsonName";

    /**
     * type predicate will be analyzed
     */
    public final static String referenceType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    /**
     * @param fileName
     *            add data from a file
     */
    public void addRdfData(String fileName) {
	try (InputStream in = Play.application().resourceAsStream(fileName)) {
	    addRdfData(in);
	} catch (Exception e) {
	    e.printStackTrace();
	    play.Logger.info("config file " + fileName + " not found.");
	}
    }

    /**
     * @param in
     *            an input stream with rdf in turtle format
     */
    public void addRdfData(InputStream in) {
	Graph g = RdfUtils.readRdfToGraph(in, RDFFormat.TURTLE, "");
	Iterator<Statement> statements = g.iterator();
	Map<String, Etikett> collect = new HashMap<String, Etikett>();
	while (statements.hasNext()) {
	    Statement st = statements.next();
	    String subj = st.getSubject().stringValue();
	    String obj = st.getObject().stringValue();
	    String pred = st.getPredicate().stringValue();
	    Etikett e = collect.get(subj);
	    if (e == null) {
		e = getValue(subj);
		if (e == null) {
		    e = new Etikett(subj);
		}
	    }
	    if (prefLabel.equals(pred)) {
		e.label = obj;
	    } else if (icon.equals(pred)) {
		e.icon = obj;
	    } else if (name.equals(pred)) {
		play.Logger.info(subj + "," + pred + "," + obj);
		e.name = obj;
	    } else if (referenceType.equals(pred)) {
		e.referenceType = obj;
	    }
	    play.Logger.info(e.uri + " " + e.name);
	    collect.put(subj, e);

	}
	Ebean.save(collect.values());
    }

    /**
     * @return all Values from etikett store
     */
    public Collection<? extends Etikett> getValues() {
	return Ebean.find(Etikett.class).findList();
    }

    /**
     * @param urlAddress
     * @return data associated with the url
     */
    public Etikett getValue(String urlAddress) {
	Etikett result = Ebean.find(Etikett.class).where()
		.eq("uri", urlAddress).findUnique();
	if (result == null) {
	    result = createLabel(urlAddress);
	    if (result.label != null)
		result.save();
	}
	return result;
    }

    private Etikett createLabel(String urlAddress) {
	Etikett etikett = new Etikett(urlAddress);
	etikett.label = lookUpLabel(urlAddress);
	return etikett;
    }

    private String lookUpLabel(String urlAddress) {
	if (urlAddress.startsWith(GndLabelResolver.id)) {
	    return GndLabelResolver.lookup(urlAddress);
	} else {
	    return DefaultLabelResolver.lookup(urlAddress);
	}
    }

}
