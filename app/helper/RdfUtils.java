/*Copyright (c) 2015 "hbz"

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
package helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.LanguageHandler;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class RdfUtils {

    /**
     * @param inputStream
     *            an Input stream containing rdf data
     * @param inf
     *            the rdf format
     * @param baseUrl
     *            see sesame docu
     * @return a Graph representing the rdf in the input stream
     */
    public static Collection<Statement> readRdfToGraph(InputStream inputStream, RDFFormat inf, String baseUrl) {
        try {
            RDFParser rdfParser = Rio.createParser(inf);
            StatementCollector collector = new StatementCollector();
            rdfParser.setRDFHandler(collector);
            ParserConfig parserConfig = rdfParser.getParserConfig();
            System.out.println(parserConfig.toString());
            parserConfig.set(BasicParserSettings.LANGUAGE_HANDLERS, Collections.<LanguageHandler> emptyList());
            rdfParser.setParserConfig(parserConfig);
            rdfParser.parse(inputStream, baseUrl);
            return collector.getStatements();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param url
     *            url to get Rdf data from
     * @param inf
     *            expected rdf serilization
     * @param accept
     *            accept header
     * @return a Rdf-Graph
     * @throws IOException
     */
    public static Collection<Statement> readRdfToGraph(URL url, RDFFormat inf, String accept) throws IOException {
        try (InputStream in = URLUtil.urlToInputStream(url, URLUtil.mapOf("Accept", accept))) {
            return readRdfToGraph(in, inf, url.toString());
        } catch (Exception e) {
            play.Logger.error("Skip use of RDFUtils due to wrong response type " + url);
        }

    }

    public static RepositoryConnection readRdfInputStreamToRepository(InputStream is, RDFFormat inf) {
        RepositoryConnection con = null;
        try {
            Repository myRepository = new SailRepository(new MemoryStore());
            myRepository.initialize();
            con = myRepository.getConnection();
            String baseURI = "";
            con.add(is, baseURI, inf);
            return con;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Literal normalizeLiteral(Literal l) {
        ValueFactory v = SimpleValueFactory.getInstance();
        Literal newLiteral = null;
        if (l.getLanguage().isPresent()) {
            String l_lang = l.getLanguage().get();
            newLiteral = v.createLiteral(Normalizer.normalize(l.stringValue().trim(), Normalizer.Form.NFKC), l_lang);
        } else {
            newLiteral = v.createLiteral(Normalizer.normalize(l.stringValue().trim(), Normalizer.Form.NFKC));
        }
        return newLiteral;
    }
}
