/**
 * 
 */
package helper;

/**
 * @author aquast
 *
 */
public class WorldcatDummyLabelResolver implements LabelResolver {

    public static final String DOMAIN = "www.worldcat.org";

    @Override
    public String lookup(String urlString, String Language) {
        // TODO Auto-generated method stub
        play.Logger.debug("This is the DummyLabelResolver: Just doing nothing");
        return urlString;
    }

}
