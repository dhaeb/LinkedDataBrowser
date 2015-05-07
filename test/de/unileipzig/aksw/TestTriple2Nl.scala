package de.unileipzig.aksw

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype
import com.hp.hpl.jena.graph.{Triple, NodeFactory, Node}
import com.hp.hpl.jena.vocabulary.RDF
import org.aksw.triple2nl.TripleConverter
import org.dllearner.kb.sparql.SparqlEndpoint
import org.junit.Assert._
import org.scalatest.FunSuite
import scala.collection.JavaConversions._

/**
 * Created by Dan Hberlein on 06.05.2015.
 */
class TestTriple2Nl extends FunSuite  {
  private val ENDPOINT_DBPEDIA: SparqlEndpoint = SparqlEndpoint.getEndpointDBpedia

  test("Library test for Triple2Nl"){
    val testable = new TripleConverter(ENDPOINT_DBPEDIA, "cache", null);
    val subject: Node = NodeFactory.createURI("http://dbpedia.org/resource/Albert_Einstein")
    val firstTriple: Triple = Triple.create(subject, RDF.`type`.asNode, NodeFactory.createURI("http://dbpedia.org/ontology/Person"))
    val secondTriple: Triple = Triple.create(subject, NodeFactory.createURI("http://dbpedia.org/ontology/birthPlace"), NodeFactory.createURI("http://dbpedia.org/resource/Ulm"))
    val triples = firstTriple :: secondTriple :: Nil
    var text: String = testable.convertTriplesToText(triples)
    System.out.println(triples + "\n-> " + text)
    assertEquals("Albert Einstein is a person, whose's birth place is Ulm.", text)
  }
}
