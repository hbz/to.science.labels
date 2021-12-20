/**
 * 
 */
package helper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author aquast
 *
 */
public class ResearchOrganizationLabelResolver extends LabelResolverService implements LabelResolver {

    public ResearchOrganizationLabelResolver() {
    }

    public final static String DOMAIN = "ror.org";

    public String lookup(String uri, String language) {
        String label = null;
        play.Logger.info("Search label for: " + uri);
        GenericPropertiesLoader genProp = new GenericPropertiesLoader();
        Map<String, String> map = genProp.loadVocabMap("ResearchOrganizationsRegistry-de.properties");
        if (map.get(uri) != null) {
            play.Logger.info("Found Label " + map.get(uri) + "for: " + uri);
            label = map.get(uri);
        } else {
            label = "Testlauf";
        }

    }

    @Override
    protected void lookupAsync(String uri, String language) {
        play.Logger.info("Search label for: " + uri);
        GenericPropertiesLoader genProp = new GenericPropertiesLoader();
        Map<String, String> map = genProp.loadVocabMap("ResearchOrganizationsRegistry-de.properties");
        if (map.get(uri) != null) {
            play.Logger.info("Found Label " + map.get(uri) + "for: " + uri);
            etikett.setUri(uri);
            etikett.setLabel(map.get(uri));
        } else {
            etikett.setUri(uri);
            etikett.setLabel("testLabel");
        }
    }
}
