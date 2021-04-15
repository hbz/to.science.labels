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
public class ToscienceApiLabelResolver extends LabelResolverService implements LabelResolver {

    public ToscienceApiLabelResolver() {
        super();
    }

    public final static String DOMAIN = EtikettMaker.TOSCIENCE_API_URL;

    public void lookupAsync(String uri, String language) {
        play.Logger.info("Lookup Value from Local API. Language selection is not supported yet! " + uri);
        if (isW3SkosUrl(uri)) {
            try {
                label = lookupSparql(uri, language, RDFFormat.RDFXML, "application/rdf+xml");
                etikett.setLabel(label);
                cacheEtikett(etikett);
            } catch (Exception e) {
                try {
                    label = lookupSparql(uri, language, RDFFormat.NTRIPLES, "text/plain");
                    etikett.setLabel(label);
                    cacheEtikett(etikett);
                } catch (Exception e2) {
                    try {
                        label = lookupSparql(uri, language, RDFFormat.JSONLD, "application/json");
                    } catch (Exception e3) {
                        label = uri;
                        etikett.setLabel(label);
                        cacheEtikett(etikett);

                    }
                }
            }
        }
    }

    public String lookupSparql(String uri, String language, RDFFormat format, String accept) {
        SparqlLookup SpL = new SparqlLookup();
        try {
            return SpL.lookup(uri, "<" + uri + ">", "http://www.w3.org/2004/02/skos/core#prefLabel", language, format,
                    accept);
        } catch (Exception e) {
            throw new RuntimeException("No label found for " + uri + "!", e);
        }
    }

    private boolean isW3SkosUrl(String urlString) {
        boolean isFunderUrl = false;
        if (urlString.contains("/2004/02/skos")) {
            isFunderUrl = true;
        }
        return isFunderUrl;
    }

}
