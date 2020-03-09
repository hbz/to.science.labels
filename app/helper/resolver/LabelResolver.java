package helper.resolver;

public class LabelResolver implements LabelResolverInterface {

    private static LabelResolver resolver = null;

    public LabelResolver() {

    }

    private LabelResolver(String className) {
        init(className);
    }

    private void init(String className) {
        try {
            resolver = (LabelResolver) Class.forName(className).newInstance();
            System.out.println(resolver.getClass().getName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(e.getMessage());
        }
    }

    public static class Factory {

        public static LabelResolver getInstance(String className) {
            return LabelResolverHolder.getLabelResolver(className);
        }
    }

    private static class LabelResolverHolder {

        private static LabelResolver getLabelResolver(String className) {
            new LabelResolver(className);
            return LabelResolver.resolver;
        }

    }

    public static String lookup(String Url, String Lang) {
        return null;
    }
}