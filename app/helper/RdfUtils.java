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
import java.util.Collection;
import java.util.Collections;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.LanguageHandler;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.StatementCollector;

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
        try (InputStream in = urlToInputStream(url, accept)) {
            return readRdfToGraph(in, inf, url.toString());
        }
    }

    static InputStream urlToInputStream(URL url, String accept) {
        HttpURLConnection con = null;
        InputStream inputStream = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept", accept);
            con.connect();
            int responseCode = con.getResponseCode();
            play.Logger.debug("Request for " + accept + " from " + url.toExternalForm());
            play.Logger.debug("Get a " + responseCode + " from " + url.toExternalForm());
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == 307 || responseCode == 303) {
                String redirectUrl = con.getHeaderField("Location");
                try {
                    URL newUrl = new URL(redirectUrl);
                    play.Logger.debug("Redirect to Location: " + newUrl);
                    return urlToInputStream(newUrl, accept);
                } catch (MalformedURLException e) {
                    URL newUrl = new URL(url.getProtocol() + "://" + url.getHost() + redirectUrl);
                    play.Logger.debug("Redirect to Location: " + newUrl);
                    return urlToInputStream(newUrl, accept);
                }
            }
            inputStream = con.getInputStream();
            return inputStream;
        } catch (IOException e) {
            play.Logger.debug("", e);
            throw new RuntimeException(e);
        }

    }
}
