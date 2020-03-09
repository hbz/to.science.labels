/**
 * 
 */
package helper.resolver;

/**
 * @author aquast
 *
 */
public interface LabelResolverInterface {

    public static String lookup(String uri, String language) {
        return null;
    };

    public String getResolverDomain();

    public String getLabelResolverClassName();

}
