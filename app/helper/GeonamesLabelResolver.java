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

/**
 * @author Jan Schnasse
 *
 */
public class GeonamesLabelResolver extends LabelResolverService implements LabelResolver {

    public GeonamesLabelResolver() {
        super();
    }

    public final static String DOMAIN = "www.geonames.org";

    public String lookup(String uri, String language) {
        SparqlLookup SpL = new SparqlLookup();
        try {
            return SpL.lookup(uri + "/about.rdf", "?s", "http://www.geonames.org/ontology#name", language,
                    RDFFormat.RDFXML, "application/rdf+xml");
        } catch (Exception e) {
            return uri;
        }
    }

    @Override
    protected void lookupAsync(String uri, String language) {
        // TODO Auto-generated method stub

    }

}
