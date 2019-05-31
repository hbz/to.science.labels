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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.rio.RDFFormat;

import com.google.common.net.UrlEscapers;

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
        String label = lookupLabelInCorrectLanguage(uri, language, format, accept);
        if (label != null) {
            return label;
        }
        label = lookupLabelInAnyLanguage(uri, format, accept);
        if (label != null) {
            return label;
        }
        return uri;
    }

    private static String lookupLabelInAnyLanguage(String uri, RDFFormat format, String accept) {
        String queryString = String.format("SELECT ?s ?o {?s <%s> ?o . }", prefLabel);
        return lookupLabel(uri, format, accept, queryString);
    }

    private static String lookupLabelInCorrectLanguage(String uri, String language, RDFFormat format, String accept) {
        if (language == null) {
            return null;
        }
        String queryString = String.format("SELECT ?s ?o {?s <%s> ?o . FILTER(LANGMATCHES(lang(?o),'%s'))}", prefLabel,
                language);
        return lookupLabel(uri, format, accept, queryString);
    }

    private static String lookupLabel(String uri, RDFFormat format, String accept, String queryString) {
        Map<String, String> args = new HashMap<>();
        args.put("accept", accept);
        try (RepositoryConnection con = RdfUtils
                .readRdfInputStreamToRepository(URLUtil.urlToInputStream(new URL(uri), args), format);) {
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            try (TupleQueryResult qresult = tupleQuery.evaluate()) {
                while (qresult.hasNext()) {
                    BindingSet bindingSet = qresult.next();
                    Value object = bindingSet.getValue("o");
                    if (object instanceof Literal) {
                        return normalizeLiteral((Literal) object);
                    }
                }
                return null;
            } catch (Exception e) {
                play.Logger.debug("Failed to find label for " + uri, e);
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String normalizeLiteral(Literal l) {
        ValueFactory v = SimpleValueFactory.getInstance();
        Literal newLiteral;
        if (l.getLanguage().isPresent()) {
            String l_lang = l.getLanguage().get();
            newLiteral = v.createLiteral(Normalizer.normalize(l.stringValue(), Normalizer.Form.NFKC), l_lang);
        } else {
            newLiteral = v.createLiteral(Normalizer.normalize(l.stringValue(), Normalizer.Form.NFKC));
        }
        return newLiteral.stringValue().trim();
    }
}
