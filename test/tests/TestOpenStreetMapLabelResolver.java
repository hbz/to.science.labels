package tests;

import org.junit.Assert;
import org.junit.Test;

import helper.EtikettMaker;
import helper.OpenStreetMapLabelResolver;

public class TestOpenStreetMapLabelResolver {
    @Test
    public void test() {
        String label = OpenStreetMapLabelResolver.lookup("https://www.openstreetmap.org/?mlat=-33.933&mlon=18.8641",
                null);
        Assert.assertEquals("-33.933,18.8641", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("https://www.openstreetmap.org/?mlat=-33.933&mlon=18.8641", null);
        Assert.assertEquals("-33.933,18.8641", label);
    }
}
