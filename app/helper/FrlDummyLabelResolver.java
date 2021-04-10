/**
 * 
 */
package helper;

/**
 * @author aquast
 *
 */
public class FrlDummyLabelResolver implements LabelResolver {

    public static final String DOMAIN = "repository.publisso.de";

    @Override
    public String lookup(String urlString, String Language) {
        // TODO Auto-generated method stub
        play.Logger.debug("This is the DummyLabelResolver: Just doing nothing");
        return urlString;
    }

}
