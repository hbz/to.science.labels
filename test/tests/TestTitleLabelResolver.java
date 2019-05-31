package tests;

import org.junit.Assert;
import org.junit.Test;

import helper.EtikettMaker;
import helper.TitleLabelResolver;

public class TestTitleLabelResolver {
    @Test
    public void test() {
        String label = TitleLabelResolver.lookup("http://lobid.org/resources/HT018920238#!", null);
        Assert.assertEquals("Digitales Archiv NRW", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("http://lobid.org/resources/HT018920238#!", null);
        Assert.assertEquals("Digitales Archiv NRW", label);
    }
}
