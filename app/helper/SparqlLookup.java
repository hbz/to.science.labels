package helper;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;

public class SparqlLookup {

    public static String lookup(String rdfAddress, String uri, String labelPredicate, String language, RDFFormat format,
            String accept) {
        String label = lookupLabelInCorrectLanguage(rdfAddress, uri, labelPredicate, language, format, accept);
        if (label != null) {
            return label;
        }
        label = lookupLabelInAnyLanguage(rdfAddress, uri, labelPredicate, format, accept);
        if (label != null) {
            return label;
        }
        return uri;
    }

    private static String lookupLabelInAnyLanguage(String rdfAddress, String uri, String labelPredicate,
            RDFFormat format, String accept) {
        String queryString = String.format("SELECT ?s ?o {<%s> <%s> ?o . }", uri, labelPredicate);
        return SparqlLookup.sparqlLabelLookup(rdfAddress, format, accept, queryString);
    }

    private static String lookupLabelInCorrectLanguage(String rdfAddress, String uri, String labelPredicate,
            String language, RDFFormat format, String accept) {
        if (language == null) {
            return null;
        }
        String queryString = String.format("SELECT ?s ?o {<%s> <%s> ?o . FILTER(LANGMATCHES(lang(?o),'%s'))}", uri,
                labelPredicate, language);
        return SparqlLookup.sparqlLabelLookup(rdfAddress, format, accept, queryString);
    }

    private static String sparqlLabelLookup(String rdfAddress, RDFFormat format, String accept, String queryString) {
        Map<String, String> args = new HashMap<>();
        args.put("accept", accept);
        try (RepositoryConnection con = RdfUtils
                .readRdfInputStreamToRepository(URLUtil.urlToInputStream(new URL(rdfAddress), args), format);) {
            TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            try (TupleQueryResult qresult = tupleQuery.evaluate()) {
                while (qresult.hasNext()) {
                    BindingSet bindingSet = qresult.next();
                    Value object = bindingSet.getValue("o");
                    if (object instanceof Literal) {
                        return RdfUtils.normalizeLiteral((Literal) object).stringValue();
                    }
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
