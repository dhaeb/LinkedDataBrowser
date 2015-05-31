package de.aksw.library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.hp.hpl.jena.query.*;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.SimpleSelector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class JenaTurtleLibraryTest {
	private Model model = RDFDataMgr.loadModel("test-surface-forms.ttl");
	final String dcat = "http://www.w3.org/2004/02/skos/core#altLabel";
	Property p = model.createProperty(dcat);

	@Test
	public void testJena() throws Exception {
		System.out.println(model.size());
		ResIterator datasets = model.listSubjects();
		long count = 0, labels = 0;
		
		while (datasets.hasNext()) {
			Resource dataset = datasets.next();
			count++;
			NodeIterator listObjectsOfProperty = model.listObjectsOfProperty(dataset, p);
			while(listObjectsOfProperty.hasNext()){
				listObjectsOfProperty.next();
				++labels;
			}
		}
		System.out.println("entities " + count);
		System.out.println("labels " + labels);
		System.out.println("cum " + (count + labels));
	}
	
	@Test
	public void createSubsetFromFile() throws IOException{
		Resource r = model.createResource("http://dbpedia.org/resource/SWAT");
		SimpleSelector s = new SimpleSelector(r,(Property)null,(RDFNode)null);
		Model simpleModel = ModelFactory.createDefaultModel().add(model.listStatements(s));
		simpleModel.write(Files.newBufferedWriter(Paths.get("./test.ttl")), "TURTLE");
	}


	@Test
	public void executeSparqlQuery(){
		Query q = QueryFactory.create("PREFIX dbres: <http://dbpedia.org/resource/>\n" +
				"\n" +
				"CONSTRUCT{ <http://dbpedia.org/resource/SWAT> ?p ?o}\n" +
				"WHERE {\n" +
				"  \n" +
				"  dbres:SWAT ?p ?o .\n" +

				"}");
		Model result = QueryExecutionFactory.create(q, model).execConstruct();
		System.out.println(result.toString());
		assertFalse("the constructed model should be non empty!", result.isEmpty());
		assertEquals(17, result.size());
	}
}
