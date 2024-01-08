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
package helper;

import org.eclipse.rdf4j.rio.RDFFormat;

import models.Etikett;

/**
 * @author Jan Schnasse
 *
 */
public class LobidLabelResolver extends LabelResolverService implements LabelResolver {

    String dcterm = "";

    public LobidLabelResolver() {
        super();
        this.dcterm = "title";
    }

    public LobidLabelResolver(String dcterm) {
        super();
        this.dcterm = dcterm;
    }

    public final static String DOMAIN = "lobid.org";

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
            play.Logger.debug("Start lookup of LobidLabelResolver for uri " + uri + "; dcterms=" + dcterm);
            label = SpL.lookup(rdfUri, "<" + rdfUri + "#!>", "http://purl.org/dc/terms/" + dcterm, language,
                    RDFFormat.JSONLD, "application/json");
            play.Logger.debug("LobidLabelResolver: label=" + label);
            if (rdfAddress.equals(label)) {
                label = SpL.lookup(rdfUri, "<" + rdfUri + ">", "http://purl.org/dc/terms/" + dcterm, language,
                        RDFFormat.JSONLD, "application/json");
                play.Logger.debug("LobidLabelResolver: label=" + label);
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
