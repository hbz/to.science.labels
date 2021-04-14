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

import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jan Schnasse
 *
 */
public class OpenStreetMapLabelResolver extends LabelResolverService implements LabelResolver {

    public final static String DOMAIN = "www.openstreetmap.org";

    public String lookup(String uri, String language) {
        play.Logger.info("Lookup Label from OSM. Language selection is not supported yet! " + uri);

        try {
            URL url = new URL(uri);
            Map<String, String> map = new LinkedHashMap<String, String>();
            String query = url.getQuery();
            for (String pair : query.split("&")) {
                String[] keyValue = pair.split("=");
                // int idx = pair.indexOf("=");
                map.put(URLDecoder.decode(keyValue[0], "UTF-8"), URLDecoder.decode(keyValue[1], "UTF-8"));
            }
            return map.get("mlat").trim() + "," + map.get("mlon").trim();
        } catch (Exception e) {
            play.Logger.debug("Failed to find label for " + uri, e);
        }
        return uri;
    }

    @Override
    protected void lookupAsync(String uri, String language) {
        // TODO Auto-generated method stub

    }
}
