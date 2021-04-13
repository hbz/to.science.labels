/**
 * 
 */
package helper;

/**
 * @author aquast
 *
 */
public class LocDummyLabelResolver implements LabelResolver {

    public static final String DOMAIN = "www.loc.gov";

    @Override
    public String lookup(String urlString, String Language) {
        // TODO Auto-generated method stub
        play.Logger.debug("This is the DummyLabelResolver: Just doing nothing");
        return urlString;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
