package tests;

import org.junit.Assert;
import org.junit.Test;

import helper.DefaultLabelResolver;
import helper.EtikettMaker;

public class TestDefaultLabelResolver {
    @Test
    public void test() {
        String label = DefaultLabelResolver.lookup("http://aims.fao.org/aos/agrovoc/c_13551", "de");
        Assert.assertEquals("Kartoffel", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("http://aims.fao.org/aos/agrovoc/c_13551", "de");
        Assert.assertEquals("Kartoffel", label);
    }
}
