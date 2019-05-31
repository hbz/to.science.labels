package tests;

import org.junit.Assert;
import org.junit.Test;

import helper.EtikettMaker;
import helper.LobidLabelResolver;

public class TestLobidLabelResolver {
    @Test
    public void test() {
        String label = LobidLabelResolver.lookup("https://lobid.org/resources/HT018920238", null);
        Assert.assertEquals("Digitales Archiv NRW", label);
    }

    @Test
    public void testEtikett() {
        String label = EtikettMaker.lookUpLabel("https://lobid.org/resources/HT018920238", "de");
        Assert.assertEquals("Digitales Archiv NRW", label);
    }
}
