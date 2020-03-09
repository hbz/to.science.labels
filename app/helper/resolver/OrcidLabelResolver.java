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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

/**
 * @author Jan Schnasse
 *
 */
public class OrcidLabelResolver extends LabelResolver implements LabelResolverInterface {

    final public static String protocol = "https://";
    final public static String alternateProtocol = "http://";
    final public static String namespace = "d-nb.info/standards/elementset/gnd#";
    final public static String domain = "orcid.org";

    final public static String id = alternateProtocol + domain;
    final public static String id2 = protocol + domain;

    public static String lookup(String uri, String language) {
        play.Logger.info("Lookup Label from ORCID. Language selection is not supported yet! " + uri);
        try (InputStream in = URLUtil.urlToInputStream(new URL(uri), URLUtil.mapOf("Accept", "application/json"))) {
            String str = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
            JsonNode hit = new ObjectMapper().readValue(str, JsonNode.class);
            String label = hit.at("/person/name/family-name/value").asText() + ", "
                    + hit.at("/person/name/given-names/value").asText();
            return label;
        } catch (Exception e) {
            play.Logger.debug("Failed to find label for " + uri, e);
        }
        return uri;
    }

    public String getResolverDomain() {
        return domain;
    }

    public String getLabelResolverClassName() {
        return OrcidLabelResolver.class.getCanonicalName();
    }

}
