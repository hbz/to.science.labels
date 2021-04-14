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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import models.Etikett;

/**
 * @author Jan Schnasse
 *
 */
public class CrossrefLabelResolver extends LabelResolverService implements LabelResolver {

    final public static String id = "http://dx.doi.org/10.13039";
    final public static String id2 = "https://dx.doi.org/10.13039";
    public final static String DOMAIN = "dx.doi.org";
    public String urlString = null;
    public String label = null;
    public String language = null;
    public Etikett etikett = null;

    public String lookup(String uri, String language) {
        this.urlString = uri;
        this.language = language;
        String etikettLabel = null;
        this.etikett = getEtikett(uri);
        if (etikett != null) {
            etikettLabel = etikett.getLabel();
            runLookupThread();
        } else {
            etikett = new Etikett(urlString);
            lookupAsync(urlString, language);
            etikettLabel = label;
        }
        return etikettLabel;
    }

    protected void lookupAsync(String uri, String language) {
        if (isCrossrefFunderUrl(uri)) {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Accept", "text/html");
            try (InputStream in = urlToInputStream(new URL(uri), headers)) {
                play.Logger.debug("Stream: " + in.toString());
                String str = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
                JsonNode hit = new ObjectMapper().readValue(str, JsonNode.class);
                String label = hit.at("/prefLabel/Label/literalForm/content").asText();
                if (label != null) {
                    etikett.setLabel(label);
                    cacheEtikett(etikett);
                }
            } catch (Exception e) {
                play.Logger.warn("Failed to find label for " + uri);
            }
        } else {
            play.Logger.debug("Nothing to do here: DOI is not a CrossrefFunder DOI");
        }

    }

    private boolean isCrossrefFunderUrl(String urlString) {
        boolean isFunderUrl = false;
        if (urlString.contains("10.13039")) {
            isFunderUrl = true;
        }
        return isFunderUrl;
    }

}
