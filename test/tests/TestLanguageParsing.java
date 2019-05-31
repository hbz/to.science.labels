package tests;

import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.Assert;
import org.junit.Test;

import helper.OrcidLabelResolver;
import helper.RdfUtils;
import play.libs.F.Callback;
import play.test.TestBrowser;

public class TestLanguageParsing {

    public final static String prefLabel = "http://www.w3.org/2004/02/skos/core#prefLabel";

    @Test
    public void test() throws FileNotFoundException, IOException {
        try (InputStream in = new FileInputStream("test/resources/test.nt")) {
            for (Statement s : RdfUtils.readRdfToGraph(in, RDFFormat.NTRIPLES, "")) {
                if (prefLabel.equals(s.getPredicate().stringValue())) {
                    Value rdfO = s.getObject();
                    if (rdfO instanceof Literal) {
                        Literal allPrefLabelsInTestFileHaveLanguageTags = (Literal) rdfO;
                        Assert.assertTrue(allPrefLabelsInTestFileHaveLanguageTags.getLanguage().isPresent());
                    }
                }
            }
        }

    }

    @Test
    public void testOrcidLookup() throws FileNotFoundException, IOException {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), HTMLUNIT, new Callback<TestBrowser>() {
            public void invoke(TestBrowser browser) {
                String label = OrcidLabelResolver.lookup("http://orcid.org/0000-0002-4796-6203", null);
                Assert.assertEquals("Schnasse, Jan", label);
            }
        });
    }
}
