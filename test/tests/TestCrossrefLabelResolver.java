package tests;

import org.junit.Assert;
import org.junit.Test;
import helper.CrossrefLabelResolver;
import helper.EtikettMaker;

public class TestCrossrefLabelResolver {
    @Test
    public void test() {
        String label = CrossrefLabelResolver.lookup("http://dx.doi.org/10.13039/501100007766", null);
        Assert.assertEquals("Max-Planck-Institut für Kognitions- und Neurowissenschaften", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("http://dx.doi.org/10.13039/501100007766", null);
        Assert.assertEquals("Max-Planck-Institut für Kognitions- und Neurowissenschaften", label);
    }
}
