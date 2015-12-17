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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Etikett;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

import com.avaje.ebean.Ebean;

import controllers.Globals;
import play.Play;
import play.mvc.Http;

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
                e = new Etikett(subj);
            }
            if (prefLabel.equals(pred)) {
                e.label = obj;
            } else if (icon.equals(pred)) {
                e.icon = obj;
            } else if (name.equals(pred)) {
                e.name = obj;
            } else if (referenceType.equals(pred)) {
                e.referenceType = obj;
            }
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
    public Etikett findEtikett(String urlAddress) {
        Etikett result = Ebean.find(Etikett.class).where().eq("uri", urlAddress).findUnique();
        if (result == null) {
            if ("admin".equals((String) Http.Context.current().args.get("role"))) {
                result = createLabel(urlAddress);
                if (result.label != null) {
                    result.save();
                }
            } else {
                result = new Etikett(urlAddress);
            }
        }
        return result;
    }

    public Etikett getValue(String urlAddress) {
        Etikett result = Ebean.find(Etikett.class).where().eq("uri", urlAddress).findUnique();
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

    /**
     * @param uploadData
     *            items in the list will override existing items with same uri
     *            <a href="https://ebean-orm.github.io/docs/introduction"> See
     *            ebean docu on save delete </a>
     */
    public void addJsonData(List<Etikett> uploadData) {
        play.Logger.debug("Insert " + uploadData.size() + " new labels.");
        for (Etikett e : uploadData) {
            addJsonData(e);
        }

    }

    public void addJsonData(Etikett e) {
        Etikett cur = null;
        if (e != null) {
            cur = Ebean.find(Etikett.class).where().eq("uri", e.uri).findUnique();
        }
        if (cur == null) {
            cur = new Etikett(e.uri);
        }
        cur.copy(e);
        Ebean.save(cur);
    }

    public static Map<String, Object> getContext() {
        List<Etikett> ls = new ArrayList<Etikett>(Globals.profile.getValues());
        Map<String, Object> pmap;
        Map<String, Object> cmap = new HashMap<String, Object>();
        for (Etikett l : ls) {
            if ("class".equals(l.referenceType) || l.referenceType == null || l.name == null)
                continue;
            pmap = new HashMap<String, Object>();
            pmap.put("@id", l.uri);
            pmap.put("label", l.label);
            pmap.put("icon", l.icon);

            if (!"String".equals(l.referenceType)) {
                pmap.put("@type", l.referenceType);
            }
            if (l.container != null) {
                pmap.put("@container", l.container);
            }
            cmap.put(l.name, pmap);
        }
        Map<String, Object> contextObject = new HashMap<String, Object>();
        contextObject.put("@context", cmap);
        return contextObject;
    }
}
