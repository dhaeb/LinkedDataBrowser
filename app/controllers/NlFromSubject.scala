package controllers

import java.net.URI
import java.util

import com.hp.hpl.jena.graph.Triple
import com.hp.hpl.jena.rdf.model.{Statement, StmtIterator, Model}
import controllers.SearchSuggestionController._
import de.aksw.Constants
import de.aksw.iterator.ExtendedIteratorStream
import de.aksw.sparql.{SparqlQueryCache, SparqlSubjectQueryRequest}
import org.aksw.triple2nl.TripleConverter
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.libs.json.Json._
import play.api.mvc.Action

import scala.collection.mutable.ListBuffer
import scala.util.Try
import de.aksw.Constants._
import scala.collection.JavaConverters._

object NlFromSubject {

  def index = Action { request =>
    val uriTry : Try[String] = Try(request.queryString("uri")).map(se => se.apply(0))
    val endpoint : SparqlEndpoint = request.getQueryString("endpoint").map(e => new SparqlEndpoint(URI.create(e).toURL)).getOrElse(Constants.ENDPOINT_DBPEDIA)
    if(uriTry.isSuccess){
      val blocking: Try[Model] = SparqlQueryCache.blocking(SparqlSubjectQueryRequest(endpoint.getURL.toString, uriTry.get))
      if(blocking.isSuccess){
        val triples: util.List[Triple] = ExtendedIteratorStream(blocking.get.listStatements()).map({ s =>
          Triple.create(s.getSubject.asNode(), s.getPredicate.asNode(), s.getObject.asNode())
        }).toList.asJava
        val text: String = new TripleConverter(endpoint, "triple2nl_cache", "public/wordnet/linux/dict").convertTriplesToText(triples)
        Ok(toJson(Map("nl" -> text)))
      } else {
        BadRequest("There was an error during the SPARQL request!")
      }
    } else {
      BadRequest("You need to specify the uri parameter!")
    }
  }
}