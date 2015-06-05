package de.aksw.library;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.*;
import de.aksw.package$;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class JenaTurtleLibraryTest {
    private Model model = RDFDataMgr.loadModel(package$.MODULE$.SIMPLESURFACEFORM_TURTLE_MODEL_FILENAME());
    private final String dcat = "http://www.w3.org/2004/02/skos/core#altLabel";
    private final Property p = model.createProperty(dcat);
    private final Query q = QueryFactory.create("PREFIX dbres: <http://dbpedia.org/resource/>\n" +
            "\n" +
            "CONSTRUCT{ <http://dbpedia.org/resource/SWAT> ?p ?o}\n" +
            "WHERE {\n" +
            "  \n" +
            "  dbres:SWAT ?p ?o .\n" +

            "}");

    @Test
    public void testJena() throws Exception {
        System.out.println(model.size());
        ResIterator datasets = model.listSubjects();
        long count = 0, labels = 0;
        while (datasets.hasNext()) {
            Resource dataset = datasets.next();
            count++;
            NodeIterator listObjectsOfProperty = model.listObjectsOfProperty(dataset, p);
            while (listObjectsOfProperty.hasNext()) {
                listObjectsOfProperty.next();
                ++labels;
            }
        }
        System.out.println("entities " + count);
        System.out.println("labels " + labels);
        System.out.println("cum " + (count + labels));
    }

    @Test
    public void createSubsetFromFile() throws IOException {
        Resource r = model.createResource("http://dbpedia.org/resource/SWAT");
        SimpleSelector s = new SimpleSelector(r, (Property) null, (RDFNode) null);
        Model simpleModel = ModelFactory.createDefaultModel().add(model.listStatements(s));
        simpleModel.write(Files.newBufferedWriter(Paths.get("./target/test-classes/test.ttl")), "TURTLE");
    }


    @Test
    public void executeSparqlQuery() {
        Model result = QueryExecutionFactory.create(q, model).execConstruct();
        System.out.println(result.toString());
        assertFalse("the constructed model should be non empty!", result.isEmpty());
        assertEquals(17, result.size());
    }


    @Test
    public void executeSparqlQueryOnDBPEdiaEndpoint() throws IOException {
        String dbpediaHostname = de.aksw.Constants$.MODULE$.dbpediaHostname();
        org.junit.Assume.assumeTrue("DBPedia is not available, make sure to connect to the net to execute this test", package$.MODULE$.isReachable(dbpediaHostname));
        QueryExecution qe = QueryExecutionFactory.sparqlService("http://" + dbpediaHostname + "/sparql", q);
        Model result = qe.execConstruct();
        System.out.println(result.toString());
        assertFalse("the constructed model should be non empty!", result.isEmpty());
        assertTrue(result.size() > 17);
    }

}
