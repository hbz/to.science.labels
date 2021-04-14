package helper;

import models.Etikett;

/**
 * @author aquast LabelResolverService provides the default methods required for
 *         the specific Authority Files LabelResolvers. LabelResolverService can
 *         be extended by specific LabelResolver Clsses that implement the
 *         LabelResolver Interface
 *
 */
public class LabelResolverService {

    /**
     * Default lookup Method that returns a label for an given Uri-String.
     * Either from Etikett Cache or from requesting the Url
     * 
     * @param uri
     *            the URI-String or URI-String we like to have resolved as label
     * @param language
     *            not supported
     * @return etikettLabel The label requested
     */
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

}
