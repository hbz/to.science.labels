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

    public final static String DOMAIN = "ror.org";

    @Override
    protected void lookupAsync(String uri, String language) {
        GenericPropertiesLoader genProp = new GenericPropertiesLoader();
        Map<String, String> map = genProp.loadVocabMap("ResearchOrganizationsRegistry-de.properties");
        if (map.get(uri) != null) {
            etikett.setUri(uri);
            etikett.setLabel(map.get(uri));
        }
    }
}
