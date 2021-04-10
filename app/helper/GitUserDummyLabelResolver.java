/**
 * 
 */
package helper;

/**
 * @author aquast
 *
 */
public class GitUserDummyLabelResolver implements LabelResolver {

    public static final String DOMAIN = "raw.githubusercontent.com";

    @Override
    public String lookup(String urlString, String Language) {
        // TODO Auto-generated method stub
        play.Logger.debug("This is the DummyLabelResolver: Just doing nothing");
        return urlString;
    }

}
