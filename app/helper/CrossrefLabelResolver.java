package helper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public class CrossrefLabelResolver {

    final public static String id = "http://dx.doi.org/10.13039";
    final public static String id2 = "https://dx.doi.org/10.13039";

    public static String lookup(String uri, String language) {
        play.Logger.info("Lookup Label from Crossref. Language selection is not supported yet! " + uri);
        play.Logger.debug("Use Crossref Resolver!");
        try (InputStream in = URLUtil.urlToInputStream(new URL(uri), null)) {
            String str = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
            JsonNode hit = new ObjectMapper().readValue(str, JsonNode.class);
            String label = hit.at("/prefLabel/Label/literalForm/content").asText();
            return label;
        } catch (Exception e) {
            play.Logger.warn("Failed to find label for " + uri, e);
        }
        return null;
    }

}
