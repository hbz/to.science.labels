/**
 * 
 */
package helper;

import models.Etikett;

/**
 * @author aquast
 *
 */
public class CCLabelResolver implements LabelResolver {

    public static final String DOMAIN = "creativecommons.org";

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
        }
        return etikettLabel;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    private Etikett getEtikett(String urlString) {
        EtikettMaker eMaker = new EtikettMaker();
        return eMaker.getValue(urlString);
    }

}
