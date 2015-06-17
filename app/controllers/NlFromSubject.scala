package controllers

import java.util

import com.hp.hpl.jena.graph.Triple
import com.hp.hpl.jena.rdf.model.impl.PropertyImpl
import com.hp.hpl.jena.rdf.model._
import controllers.SearchSuggestionController._
import de.aksw.iterator.ExtendedIteratorStream
import org.aksw.triple2nl.TripleConverter
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.libs.json.Json._
import play.api.mvc.Result

import scala.collection.JavaConverters._
import play.api.Logger

object NlFromSubject extends LdbController {

  override def process(uri: String, endpoint: SparqlEndpoint, m: Model): Result = {
    val text: String = createTextualRepresenationUsingTriple2Nl(endpoint, m, (s => s.getPredicate.toString.contains("http://dbpedia.org/ontology") && !inBlacklist(s))).orElse(
    {
      logger.info(s"For URI ${uri} the NL generation generated an empty string. Try again with more triples...")
      createTextualRepresenationUsingTriple2Nl(endpoint, m, (s => !inBlacklist(s)))
    }) .getOrElse({
      logger.warn(s"For URI ${uri} the extended NL generation generated an empty string. Returning it...")
      ""
    })
    Ok(toJson(Map("nl" -> text)))
  }

  def createTextualRepresenationUsingTriple2Nl(endpoint: SparqlEndpoint, m: Model, statementFilter : Statement => Boolean): Option[String] = {
    val statements: Stream[Statement] = ExtendedIteratorStream(m.listStatements())
    val triples: util.List[Triple] = (for {
      s: Statement <- statements if isClass(s) || statementFilter(s)
    } yield {
        Triple.create(s.getSubject.asNode(), s.getPredicate.asNode(), s.getObject.asNode())
      }).toList.asJava
    new TripleConverter(endpoint, "triple2nl_cache", "conf/wordnet/linux/dict").convertTriplesToText(triples) match {
      case s if s.isEmpty => None
      case s => Some(s)
    }
  }

  val classProperty: Property = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
  
  def isClass(s : Statement) : Boolean = {
    s.getPredicate.equals(classProperty)
  }

  val blackListPropertyUris = Set(
    "http://dbpedia.org/ontology/wikiPageID",
    "http://dbpedia.org/ontology/wikiPageExternalLink",
    "http://dbpedia.org/ontology/abstract",
    "http://dbpedia.org/ontology/wikiPageRevisionID",
    "http://dbpedia.org/ontology/thumbnail",
    "http://dbpedia.org/ontology/wikiHITS",
    "http://dbpedia.org/ontology/wikiPageRank",
    "http://dbpedia.org/ontology/wikiPageInLinkCountCleaned",
    "http://dbpedia.org/ontology/wikiPageOutLinkCountCleaned",
    "http://www.w3.org/2002/07/owl#sameAs"
  ).map(ResourceFactory.createProperty(_))


  val blackListObjectUris = Set(
    "http://www.w3.org/2002/07/owl#Thing"
  ).map(ResourceFactory.createResource(_))
  
  def inBlacklist(s : Statement) : Boolean = {
    val obj: RDFNode = s.getObject
    (blackListPropertyUris contains s.getPredicate) || (obj.isURIResource && (blackListObjectUris contains obj.asResource()))
  }
}