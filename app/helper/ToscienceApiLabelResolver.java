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
import java.io.InputStreamReader;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

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
        play.Logger.debug("Lookup Value from Local API. Language selection is not supported yet! " + uri);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");

        try (InputStream in = urlToInputStream(new URL(uri), headers)) {
            String str = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
            JsonNode hit = new ObjectMapper().readValue(str, JsonNode.class);
            ArrayList<String> hList = (ArrayList<String>) hit.findValuesAsText("@value");
            label = hList.get(0);
        } catch (Exception e) {
            play.Logger.debug("Can't connect to " + DOMAIN);
        }
        if (label == null) {
            label = uri;
        }
    }

}
