package helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public class OrcidLabelResolver {
    final public static String id = "http://orcid.org";

    public static String lookup(String uri) {
        play.Logger.info(uri);
        try (InputStream in = RdfUtils.urlToInputStream(new URL(uri), "application/json")) {
            String str = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
            JsonNode hit = new ObjectMapper().readValue(str, JsonNode.class);
            String label = hit.at("/person/name/family-name/value").asText() + ", "
                    + hit.at("/person/name/given-names/value").asText();
            return label;
        } catch (Exception e) {
            play.Logger.warn("", e);
        }
        return uri;
    }
}
