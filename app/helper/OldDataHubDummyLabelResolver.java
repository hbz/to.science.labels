/**
 * 
 */
package helper;

/**
 * @author aquast
 *
 */
public class OldDataHubDummyLabelResolver implements LabelResolver {

    public static final String DOMAIN = "old.datahub.io";

    @Override
    public String lookup(String urlString, String Language) {
        // TODO Auto-generated method stub
        play.Logger.debug("This is the DummyLabelResolver: Just doing nothing");
        return urlString;
    }

}
