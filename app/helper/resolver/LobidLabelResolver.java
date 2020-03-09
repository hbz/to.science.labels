/*Copyright (c) 2019 "hbz"

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

import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * @author Jan Schnasse
 * @author Andres Quast quast@hbz-nrw.de
 * 
 */
public class LobidLabelResolver extends LabelResolver implements LabelResolverInterface {

    final public static String protocol = "https://";
    final public static String alternateProtocol = "http://";
    final public static String namespace = "d-nb.info/standards/elementset/gnd#";
    final public static String domain = "lobid.org/resources";

    final public static String id = alternateProtocol + domain;
    final public static String id2 = protocol + domain;

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public static String lookup(String uri, String language) {
        try {
            String rdfAddress = uri;

            /**
             * Lobid uses http uris
             * 
             */
            String rdfUri = uri.replaceAll("https", "http");

            String label = SparqlLookup.lookup(uri, "<" + rdfUri + "#!>", "http://purl.org/dc/terms/title", language,
                    RDFFormat.JSONLD, "application/json");
            if (rdfAddress.equals(label)) {
                label = SparqlLookup.lookup(uri, "<" + rdfUri + ">", "http://purl.org/dc/terms/title", language,
                        RDFFormat.JSONLD, "application/json");
            }
            return label;
        } catch (Exception e) {
            return uri;
        }
    }

    public String getResolverDomain() {
        return domain;
    }

    public String getLabelResolverClassName() {
        return LobidLabelResolver.class.getCanonicalName();
    }

}
