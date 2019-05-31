package tests;

import org.junit.Assert;
import org.junit.Test;

import helper.EtikettMaker;
import helper.OrcidLabelResolver;

public class TestOrcidLabelResolver {
    @Test
    public void test() {
        String label = OrcidLabelResolver.lookup("http://orcid.org/0000-0002-4796-6203", null);
        Assert.assertEquals("Schnasse, Jan", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("http://orcid.org/0000-0002-4796-6203", null);
        Assert.assertEquals("Schnasse, Jan", label);
    }
}
