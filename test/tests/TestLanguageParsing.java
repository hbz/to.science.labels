package tests;

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

import helper.RdfUtils;

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
}
