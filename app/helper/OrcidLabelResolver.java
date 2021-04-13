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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import models.Etikett;

/**
 * @author Jan Schnasse
 *
 */
public class OrcidLabelResolver implements LabelResolver {
    final public static String id = "http://orcid.org";
    final public static String id2 = "https://orcid.org";
    public final static String DOMAIN = "orcid.org";
    public String urlString = null;
    public String label = null;
    public String language = null;
    public Etikett etikett = null;

    public String lookup(String uri, String language) {
        this.urlString = uri;
        this.language = language;
        this.etikett = getEtikett(uri);
        String etikettLabel = null;
        if (etikett != null) {
            String tmpLabel = etikett.getLabel();
            if (!tmpLabel.startsWith("http")) {
                etikettLabel = tmpLabel;
            }
        } else {
            etikett = new Etikett(urlString);
        }
        runLookupThread();
        return etikettLabel;
    }

    private void lookupAsync(String uri, String language) {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "application/json");

        try (InputStream in = urlToInputStream(new URL(uri), headers)) {
            String str = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
            JsonNode hit = new ObjectMapper().readValue(str, JsonNode.class);
            label = hit.at("/person/name/family-name/value").asText() + ", "
                    + hit.at("/person/name/given-names/value").asText();
        } catch (Exception e) {
            play.Logger.info("Failed to find label for " + uri);
        }
        if (label != null) {
            etikett.setLabel(label);
            cacheEtikett(etikett);
        }

    }

    private InputStream urlToInputStream(URL url, Map<String, String> args) {

        Connector hConn = Connector.Factory.getInstance(url);
        if (args != null) {
            for (Entry<String, String> e : args.entrySet()) {
                hConn.setConnectorProperty(e.getKey(), e.getValue());
            }
        }
        hConn.connect();
        return hConn.getInputStream();
    }

    @Override
    public void run() {

        lookupAsync(urlString, language);

    }

    private void runLookupThread() {

        Thread thread = new Thread(this);
        thread.start();
    }

    private Etikett getEtikett(String urlString) {
        EtikettMaker eMaker = new EtikettMaker();
        return eMaker.getValue(urlString);
    }

    private void cacheEtikett(Etikett etikett) {
        EtikettMaker eMaker = new EtikettMaker();
        eMaker.addJsonDataIntoDBCache(etikett);
    }

}
