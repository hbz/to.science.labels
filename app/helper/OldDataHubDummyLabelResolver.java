/**
 * 
 */
package helper;

/**
 * @author aquast
 *
 */
public class OldDataHubDummyLabelResolver extends LabelResolver {

    public static final String DOMAIN = "old.datahub.io";

    @Override
    public String lookup(String urlString, String Language) {
        // TODO Auto-generated method stub
        play.Logger.debug("This is the DummyLabelResolver: Just doing nothing");
        return urlString;
    }

    @Override
    protected void lookupAsync(String uri, String language) {
        // TODO Auto-generated method stub

    }

}
