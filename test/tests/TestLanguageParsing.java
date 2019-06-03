/*Copyright (c) 2019 "hbz"

This file is part of etikett.

etikett is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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

/**
 * @author Jan Schnasse
 *
 */
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
