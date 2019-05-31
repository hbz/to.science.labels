package tests;

import org.junit.Assert;
import org.junit.Test;

import helper.EtikettMaker;
import helper.GeonamesLabelResolver;

public class TestGeonamesLabelResolver {
    @Test
    public void test() {
        String label = GeonamesLabelResolver.lookup("http://www.geonames.org/3369157", null);
        Assert.assertEquals("Cape Town", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("http://www.geonames.org/3369157", "en");
        Assert.assertEquals("Cape Town", label);
    }
}
