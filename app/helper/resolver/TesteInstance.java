package helper.resolver;

public class TesteInstance {

    public static void main(String[] args) {

        LabelResolver test = LabelResolver.getInstance(OrcidLabelResolver.class.getCanonicalName());
        System.out.println("Test: " + test.getResolverDomain());
        test = LabelResolver.getInstance(GndLabelResolver.class.getCanonicalName());
        System.out.println("Test: " + test.getResolverDomain());

    }

}
