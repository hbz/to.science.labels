/**
 * 
 */
package helper.resolver;

import java.util.Properties;

/**
 * @author aquast
 *
 */
public class LabelResolverProperties {

    private static Properties resolverProp = new Properties();

    public LabelResolverProperties() {
        setLabelResolvers();
    }

    /**
     * Set Properties that takes all available LabelResolvers (extends
     * LabelResolver and implementing LabelResolverInterface). If a new
     * LabelResolver is added, please set an new Property here
     */
    private void setLabelResolvers() {
        resolverProp.setProperty(new CrossrefLabelResolver().getResolverDomain(),
                new CrossrefLabelResolver().getLabelResolverClassName());
        resolverProp.setProperty(new GeonamesLabelResolver().getResolverDomain(),
                new GeonamesLabelResolver().getLabelResolverClassName());
        resolverProp.setProperty(new GndLabelResolver().getResolverDomain(),
                new GndLabelResolver().getLabelResolverClassName());
        resolverProp.setProperty(new LobidLabelResolver().getResolverDomain(),
                new LobidLabelResolver().getLabelResolverClassName());
        resolverProp.setProperty(new LobidLabelResolver().getResolverDomain(),
                new LobidLabelResolver().getLabelResolverClassName());
        resolverProp.setProperty(new OpenStreetMapLabelResolver().getResolverDomain(),
                new OpenStreetMapLabelResolver().getLabelResolverClassName());
        resolverProp.setProperty(new OrcidLabelResolver().getResolverDomain(),
                new OrcidLabelResolver().getLabelResolverClassName());
    }

    public static Properties getLabelResolverProperties() {
        return resolverProp;
    }

}
