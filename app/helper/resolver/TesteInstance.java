package helper.resolver;

public class TesteInstance {

    public static void main(String[] args) {

        LabelResolver test = LabelResolver.Factory.getInstance(OrcidLabelResolver.class.getCanonicalName());
        System.out.println("Test: " + test);

    }

}
