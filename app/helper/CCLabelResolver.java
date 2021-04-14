/**
 * 
 */
package helper;

import models.Etikett;

/**
 * @author aquast
 *
 */
public class CCLabelResolver extends LabelResolverService implements LabelResolver {

    public static final String DOMAIN = "creativecommons.org";

    public String lookup(String uri, String language) {
        this.urlString = uri;
        this.language = language;
        String etikettLabel = null;
        this.etikett = getEtikett(uri);
        if (etikett != null) {
            etikettLabel = etikett.getLabel();
        }
        return etikettLabel;
    }

    @Override
    protected void lookupAsync(String uri, String language) {
        // TODO Auto-generated method stub

    }

}
