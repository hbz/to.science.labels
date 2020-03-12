package helper.resolver;

public class LabelResolver {

    private static LabelResolver resolver = null;

    public LabelResolver() {

    }

    private LabelResolver(String className) {
        init(className);
    }

    public String lookup(String uri, String lang) {
        return null;
    };

    public String getResolverDomain() {
        return null;
    };

    public String getResolverClassName() {
        return null;
    };

    private void init(String className) {
        try {
            resolver = (LabelResolver) Class.forName(className).newInstance();
            System.out.println(resolver.getClass().getName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static LabelResolver getInstance(String className) {
        return LabelResolverHolder.getLabelResolver(className);
    }

    private static class LabelResolverHolder {

        private static LabelResolver getLabelResolver(String className) {
            new LabelResolver(className);
            return LabelResolver.resolver;

        }

    }

}