/**
 * 
 */
package helper;

/**
 * @author aquast
 *
 */
public interface LabelResolver {

    public static String lookup(String uri, String language) {
        return null;
    };

    public String getResolverDomain();

    public String getLabelResolverClassName();

}
