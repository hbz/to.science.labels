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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import controllers.Globals;
import models.Etikett;
import models.Etikett.EtikettType;
import play.Play;
import play.mvc.Http;

/**
 * @author Jan Schnasse
 *
 */
public class EtikettMaker {

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
     * Uri used for skos concepts
     */
    public final static String skosConcept = "http://www.w3.org/2004/02/skos/core#Concept";

    private String ID_ALIAS = null;

    private String TYPE_ALIAS = null;

    public static String TOSCIENCE_PRODUCT_URL = null;
    public static String TOSCIENCE_API_URL = null;

    public EtikettMaker() {
        ID_ALIAS = Play.application().configuration().getString("etikett.alias.id");
        TYPE_ALIAS = Play.application().configuration().getString("etikett.alias.type");
        TOSCIENCE_PRODUCT_URL = Play.application().configuration().getString("toscience.domain");
        TOSCIENCE_API_URL = Play.application().configuration().getString("application.toscience.url.api");
        play.Logger.debug(TOSCIENCE_API_URL);
    }

    /**
     * @param fileName
     *            add data from a file
     */
    public void addRdfData(String fileName, String language) {
        try (InputStream in = Play.application().resourceAsStream(fileName)) {
            addRdfData(in, language);
        } catch (Exception e) {
            e.printStackTrace();
            play.Logger.info("config file " + fileName + " not found.");
        }
    }

    /**
     * @param in
     *            an input stream with rdf in turtle format
     */
    public void addRdfData(InputStream in, String language) {
        addJsonData(convertRdfData(in, language));
    }

    public List<Etikett> convertRdfData(InputStream in, String language) {
        List<Etikett> result = new ArrayList<>();
        Collection<Statement> g = new RdfUtils().readRdfToGraph(in, RDFFormat.TURTLE, "");
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
                Literal oL = (Literal) st.getObject();
                if (oL.getLanguage().isPresent()) {
                    if (language.equals(oL.getLanguage().get())) {
                        e.label = obj;
                    }
                    e.addMultilangLabel(oL.getLanguage().get(), obj);
                } else {
                    // last incoming label will win
                    e.label = obj;
                }
            } else if (icon.equals(pred)) {
                e.icon = obj;
            } else if (name.equals(pred)) {
                e.name = obj;
            } else if (referenceType.equals(pred)) {
                if (skosConcept.equals(obj)) {
                    obj = null;
                }
                e.referenceType = obj;
            }
            collect.put(subj, e);
        }
        result.addAll(collect.values());
        return result;
    }

    /**
     * @return all Values from etikett store
     */
    public List<Etikett> getValues() {
        Collection<Etikett> result = getStoreValues();
        result.addAll(getContextValues());
        return result.stream().sorted((a, b) -> {
            return a.getUri().compareTo(b.getUri());
        }).collect(Collectors.toList());
    }

    /**
     * @return all manual added values from etikett store
     */
    public List<Etikett> getStoreValues() {
        return Ebean.find(Etikett.class).where().eq("type", EtikettType.STORE).findList().stream().sorted((a, b) -> {
            return a.getUri().compareTo(b.getUri());
        }).collect(Collectors.toList());
    }

    /**
     * @return all on the fly added values from etikett store
     */
    public List<Etikett> getCacheValues() {
        return Ebean.find(Etikett.class).where().eq("type", EtikettType.CACHE).findList().stream().sorted((a, b) -> {
            return a.getUri().compareTo(b.getUri());
        }).collect(Collectors.toList());
    }

    public List<Etikett> deleteCacheValues() {
        List<Etikett> result = getCacheValues().stream().sorted((a, b) -> {
            return a.getUri().compareTo(b.getUri());
        }).collect(Collectors.toList());
        Ebean.delete(result);
        return result;
    }

    /**
     * @return all context relevant values from etikett store
     */
    public List<Etikett> getContextValues() {
        return Ebean.find(Etikett.class).where().eq("type", EtikettType.CONTEXT).findList().stream().sorted((a, b) -> {
            return a.getName().compareTo(b.getName());
        }).collect(Collectors.toList());
    }

    public Etikett getValue(String urlAddress) {
        Etikett result = null;
        result = Ebean.find(Etikett.class).where().eq("uri", urlAddress).findUnique();
        if (result != null) {
            play.Logger.debug("Fetched Label from db: " + result.label);
        }
        return result;
    }

    /**
     * @param urlAddress
     * @return data associated with the url
     */
    public Etikett findEtikett(String urlAddress) {
        Etikett result = null;
        result = getValue(urlAddress);
        try {
            if ((result == null || result.getType().equals(Etikett.EtikettType.CACHE))
                    && urlAddress.startsWith("http")) {
                play.Logger.debug("Perform Label lookup from URL: " + urlAddress);
                result = getLabelFromUrlAddress(urlAddress);
                if (result != null) {
                    addJsonDataIntoDBCache(result);
                }
            } else if (result == null && urlAddress.startsWith("genid")) {
                urlAddress = EtikettMaker.TOSCIENCE_API_URL + "/adhoc/uri/" + urlAddress.hashCode();
                // play.Logger.debug("Perform Label lookup from URL: " +
                // urlAddress);
                result.uri = urlAddress;
                result.label = "Dummy Name";
                if (result != null) {
                    addJsonDataIntoDBCache(result);
                }
            }
        } catch (Exception e) {
            play.Logger.warn("Label konnte nicht gefunden werden");
        }
        if (result == null) {
            play.Logger.debug("Due to missing Etikett, creating new Etikett with urlAdress as Label as last option: "
                    + urlAddress);
            result = new Etikett(urlAddress);
            result.label = urlAddress;
        }
        return result;

    }

    private Etikett getLabelFromUrlAddress(String urlAddress) {
        Etikett result;
        play.Logger.debug("Rolle=" + (String) Http.Context.current().args.get("role"));
        if ("admin".equals((String) Http.Context.current().args.get("role"))) {
            result = createLabel(urlAddress);
            if (result.label != null) {
                return result;
            }
        }
        return null;
    }

    private Etikett createLabel(String urlAddress) {
        try {
            Etikett etikett = new Etikett(urlAddress);
            etikett.label = lookUpLabel(URLDecoder.decode(urlAddress, "UTF-8"));
            return etikett;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String lookUpLabel(String urlAddress) {
        return lookUpLabel(urlAddress, getDefaultLanguage());
    }

    private static String getDefaultLanguage() {
        String language = Play.application().configuration().getString("etikett.language");
        if (language == null || language.isEmpty()) {
            language = null;
        }
        return language;
    }

    public String lookUpLabel(String urlAddress, String lang) {
        play.Logger.debug("Starting to look up label for String=" + urlAddress + "; Sprache=" + lang);
        String result = null;

        LabelResolver lResolver = LabelResolver.Factory.getInstance(urlAddress);
        // LabelResolver.Factory.getInstance(urlAddress);
        if (lResolver != null) {
            play.Logger.debug("Start getting label from LabelResolver: " + lResolver.toString());
            result = lResolver.lookup(urlAddress, lang);
        }
        if (result == null) {
            play.Logger.warn("No LabelResolver returned, URL will be written into Label");
            result = urlAddress;
        }
        /*
         * if (lResolver == null && result.equals(urlAddress)) { try { result =
         * DefaultLabelResolver.lookup(urlAddress, lang); if
         * (urlAddress.equals(result)) { result =
         * TitleLabelResolver.lookup(urlAddress, lang); } } catch (Exception e)
         * { play.Logger.info("Lookup fails inside the LabelResolvers"); } }
         */

        return result;
    }

    /**
     * @param uploadData
     *            items in the list will override existing items with same uri
     *            <a href="https://ebean-orm.github.io/docs/introduction"> See
     *            ebean docu on save delete </a>
     */
    public void addJsonData(Collection<Etikett> uploadData) {
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
        if ("class".equals(e.referenceType)) {
            e.referenceType = null;
        } else if (skosConcept.equals(e.referenceType)) {
            e.referenceType = null;
        }
        cur.copy(e);

        if (cur.referenceType != null && cur.referenceType.isEmpty())
            cur.referenceType = null;

        if ("class".equals(cur.referenceType) || cur.referenceType == null || cur.name == null) {
            cur.setType(Etikett.EtikettType.STORE);
        } else {
            cur.setType(Etikett.EtikettType.CONTEXT);
        }
        Ebean.save(cur);
    }

    public void addJsonDataIntoDBCache(Etikett e) {
        Etikett cur = null;
        if (e != null) {
            cur = Ebean.find(Etikett.class).where().eq("uri", e.uri).findUnique();
        }
        if (cur == null) {
            cur = new Etikett(e.uri);
        }
        if ("class".equals(e.referenceType)) {
            e.referenceType = null;
        } else if (skosConcept.equals(e.referenceType)) {
            e.referenceType = null;
        }
        cur.copy(e);

        if (cur.referenceType != null && cur.referenceType.isEmpty()) {
            cur.referenceType = null;
        }
        cur.setType(Etikett.EtikettType.CACHE);
        Ebean.save(cur);
    }

    public Map<String, Object> getContext() {
        List<Etikett> ls = getContextValues();
        Map<String, Object> pmap;
        Map<String, Object> cmap = new TreeMap<String, Object>();
        for (Etikett l : ls) {
            if ("class".equals(l.referenceType) || l.referenceType == null || l.name == null)
                continue;
            pmap = new HashMap<String, Object>();
            pmap.put("@id", l.uri);
            pmap.put("label", l.label);
            pmap.put("icon", l.icon);
            pmap.put("weight", l.weight);
            pmap.put("comment", l.comment);
            if (!"String".equals(l.referenceType)) {
                pmap.put("@type", l.referenceType);
            }
            if (l.container != null && !l.container.isEmpty()) {
                pmap.put("@container", l.container);
            }
            cmap.put(l.name, pmap);
        }
        addAliases(cmap);
        Map<String, Object> contextObject = new TreeMap<String, Object>();
        contextObject.put("@context", cmap);
        return contextObject;
    }

    private void addAliases(Map<String, Object> cmap) {
        // play.Logger.debug(ID_ALIAS + ":@id , " + TYPE_ALIAS + ":@type");
        if (ID_ALIAS != null) {
            cmap.put(ID_ALIAS, "@id");
        }
        if (TYPE_ALIAS != null) {
            cmap.put(TYPE_ALIAS, "@type");
        }
    }

    public void addJsonContextData(Map<String, Object> contextMap) {
        List<Etikett> result = convertJsonContextData(contextMap);
        addJsonData(result);
    }

    public List<Etikett> convertJsonContextData(Map<String, Object> contextMap) {
        List<Etikett> result = new ArrayList<Etikett>();
        @SuppressWarnings("unchecked")
        Map<String, Object> c = (Map<String, Object>) contextMap.get("@context");
        for (String fieldName : c.keySet()) {
            play.Logger.debug("" + fieldName);
            if (c.get(fieldName) instanceof Map<?, ?>) {
                Map<String, Object> contextEntry = (Map<String, Object>) c.get(fieldName);
                Etikett e = new Etikett((String) contextEntry.get("@id"));
                e.name = fieldName;
                e.label = (String) contextEntry.get("label");
                e.referenceType = "String";
                String type = (String) contextEntry.get("@type");
                if (type != null) {
                    e.referenceType = type;
                }
                e.container = (String) contextEntry.get("@container");
                e.icon = (String) contextEntry.get("icon");
                e.weight = (String) contextEntry.get("weight");
                e.comment = (String) contextEntry.get("comment");
                result.add(e);
                play.Logger.debug("" + e);
            }
        }
        return result;
    }

    public Map<String, Object> getRawContext() {
        List<Etikett> ls = new ArrayList<Etikett>(Globals.profile.getValues());
        Map<String, Object> pmap;
        Map<String, Object> cmap = new TreeMap<String, Object>();
        for (Etikett l : ls) {
            if ("class".equals(l.referenceType) || l.referenceType == null || l.name == null)
                continue;
            pmap = new HashMap<String, Object>();
            pmap.put("@id", l.uri);
            if (!"String".equals(l.referenceType)) {
                pmap.put("@type", l.referenceType);
            }
            if (l.container != null && !l.container.isEmpty()) {
                pmap.put("@container", l.container);
            }
            cmap.put(l.name, pmap);
        }

        addAliases(cmap);
        Map<String, Object> contextObject = new HashMap<String, Object>();
        contextObject.put("@context", cmap);

        return contextObject;
    }

    public Map<String, Object> getContextAnnotation() {
        List<Etikett> ls = new ArrayList<Etikett>(Globals.profile.getValues());
        Map<String, Object> pmap;
        Map<String, Object> cmap = new TreeMap<String, Object>();
        for (Etikett l : ls) {
            if ("class".equals(l.referenceType) || l.referenceType == null || l.name == null)
                continue;
            pmap = new HashMap<String, Object>();
            pmap.put("id", l.uri);
            pmap.put("label", l.label);
            pmap.put("icon", l.icon);
            pmap.put("weight", l.weight);
            pmap.put("comment", l.comment);
            cmap.put(l.name, pmap);
        }
        Map<String, Object> contextObject = new HashMap<String, Object>();
        contextObject.put("context-annotation", cmap);
        return contextObject;
    }

    /**
     * @param object
     *            a java object
     * @return a json serialization of the object as string
     */
    public static String json(Object object) {
        try {
            return new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(object);
        } catch (Exception e) {
            return "To String failed " + e.getMessage();
        }
    }
}
