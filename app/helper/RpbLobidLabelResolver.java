/*Copyright (c) 2024 "hbz"

This file is part of labels ehem. etikett

labels is free software: you can redistribute it and/or modify
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
import models.Etikett;

/**
 * @author Ingolf Kuss, hbz
 *
 */
public class RpbLobidLabelResolver extends LabelResolverService implements LabelResolver {

    public RpbLobidLabelResolver() {
        super();
    }

    public final static String DOMAIN = "rpb.lobid.org";

    /**
     * @param uri
     *            analyes data from the url to find a proper label
     * @return a label
     */
    public void lookupAsync(String uri, String language) {
        try {
            String rdfAddress = uri;

            /**
             * Lobid uses http uris
             * 
             */
            String rdfUri = uri.replaceAll("https", "http");

            SparqlLookup SpL = new SparqlLookup();
            play.Logger.debug("Start lookup of RpbLobidLabelResolver for uri " + uri);
            label = SpL.lookup("https://raw.githubusercontent.com/hbz/lobid-vocabs/master/rpb/rpb-spatial.ttl",
                    "<" + rdfUri + "#!>", "http://www.w3.org/2004/02/skos/core#prefLabel", language, RDFFormat.TURTLE,
                    "application/json");
            play.Logger.debug("RpbLobidLabelResolver: label=" + label);
            if (rdfAddress.equals(label)) {
                // 2. Versuch ohne Hashtag
                label = SpL.lookup("https://raw.githubusercontent.com/hbz/lobid-vocabs/master/rpb/rpb-spatial.ttl",
                        "<" + rdfUri + ">", "http://www.w3.org/2004/02/skos/core#prefLabel", language, RDFFormat.TURTLE,
                        "application/json");
                play.Logger.debug("RpbLobidLabelResolver: label=" + label);
            }
            etikett.setLabel(label);
            cacheEtikett(etikett);
        } catch (Exception e) {
            play.Logger.warn(e.toString());
            label = uri;
            etikett.setLabel(label);
        }
    }

    @Override
    public void run() {
        lookupAsync(urlString, language);

    }

}