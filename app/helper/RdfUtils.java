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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import org.openrdf.model.Graph;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.TreeModel;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.StatementCollector;
import org.openrdf.sail.memory.MemoryStore;

/**
 * @author Jan Schnasse schnasse@hbz-nrw.de
 * 
 */
public class RdfUtils {

    /**
     * @param url
     *            A url to read from
     * @param inf
     *            the rdf format of the url's data
     * @param accept
     *            the accept header
     * @return a Graph with the rdf
     * @throws IOException
     */
    public static Graph readRdfToGraph(URL url, RDFFormat inf, String accept)
	    throws IOException {
	try (InputStream in = urlToInputStream(url, accept)) {
	    return readRdfToGraph(in, inf, url.toString());
	}
    }

    private static InputStream urlToInputStream(URL url, String accept) {
	URLConnection con = null;
	InputStream inputStream = null;
	try {
	    con = url.openConnection();
	    con.setRequestProperty("Accept", accept);
	    con.connect();
	    inputStream = con.getInputStream();
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
	return inputStream;
    }

    /**
     * @param inputStream
     *            an Input stream containing rdf data
     * @param inf
     *            the rdf format
     * @param baseUrl
     *            see sesame docu
     * @return a Graph representing the rdf in the input stream
     */
    public static Graph readRdfToGraph(InputStream inputStream, RDFFormat inf,
	    String baseUrl) {
	try {
	    RDFParser rdfParser = Rio.createParser(inf);
	    org.openrdf.model.Graph myGraph = new TreeModel();
	    StatementCollector collector = new StatementCollector(myGraph);
	    rdfParser.setRDFHandler(collector);
	    rdfParser.parse(inputStream, baseUrl);
	    return myGraph;
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * @param subject
     *            the triples subject
     * @param predicate
     *            the triples predicate
     * @param object
     *            the triples object
     * @param isLiteral
     *            true, if object is a literal
     * @param metadata
     *            ntriple rdf-string to add the triple
     * @param format
     *            format of in and out
     * @return the string together with the new triple
     */
    public static String addTriple(String subject, String predicate,
	    String object, boolean isLiteral, String metadata, RDFFormat format) {
	try {
	    RepositoryConnection con = null;
	    if (metadata != null) {
		InputStream is = new ByteArrayInputStream(
			metadata.getBytes("UTF-8"));
		con = readRdfInputStreamToRepository(is, format);
	    } else {
		Repository myRepository = new SailRepository(new MemoryStore());
		myRepository.initialize();
		con = myRepository.getConnection();
	    }
	    ValueFactory f = con.getValueFactory();
	    URI s = f.createURI(subject);
	    URI p = f.createURI(predicate);
	    Value o = null;
	    if (!isLiteral) {
		o = f.createURI(object);
	    } else {
		o = f.createLiteral(object);
	    }
	    con.add(s, p, o);
	    return writeStatements(con, format);
	} catch (RepositoryException e) {
	    throw new RuntimeException(e);
	} catch (UnsupportedEncodingException e) {
	    throw new RuntimeException(e);
	}
    }

    private static RepositoryConnection readRdfInputStreamToRepository(
	    InputStream is, RDFFormat inf) {
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

    private static String writeStatements(RepositoryConnection con,
	    RDFFormat outf) {
	StringWriter out = new StringWriter();
	RDFWriter writer = Rio.createWriter(outf, out);
	String result = null;
	try {
	    writer.startRDF();
	    RepositoryResult<Statement> statements = con.getStatements(null,
		    null, null, false);

	    while (statements.hasNext()) {
		Statement statement = statements.next();
		writer.handleStatement(statement);
	    }
	    writer.endRDF();
	    result = out.toString();

	} catch (RDFHandlerException e) {
	    throw new RuntimeException(e);
	} catch (RepositoryException e) {
	    throw new RuntimeException(e);
	}
	return result;
    }
}
