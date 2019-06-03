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

import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * @author Jan Schnasse
 *
 */
@SuppressWarnings("javadoc")
public class DefaultLabelResolver {
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
                try {
                    return lookup(uri, language, RDFFormat.JSONLD, "application/json");
                } catch (Exception e3) {
                    return uri;
                }
            }
        }
    }

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public static String lookup(String uri, String language, RDFFormat format, String accept) {
        try {
            return SparqlLookup.lookup(uri, "<" + uri + ">", "http://www.w3.org/2004/02/skos/core#prefLabel", language,
                    format, accept);
        } catch (Exception e) {
            play.Logger.debug("", e);
            throw new RuntimeException("No label found for " + uri + "!", e);
        }
    }
}
