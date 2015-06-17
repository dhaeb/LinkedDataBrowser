package controllers

import com.hp.hpl.jena.rdf.model._
import controllers.SearchSuggestionController._
import de.aksw.Constants
import de.aksw.iterator.ExtendedIteratorStream
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.libs.json.Json._
import play.api.mvc.{AnyContent, Request, Result}

import scala.util.Try

object RdfFromSubject extends LdbController {

  val DEFAULT_SIZE: Int = 10

  val p = ResourceFactory.createProperty("http://dbpedia.org/ontology/wikiPageRank")
  override def process(uri: String, endpoint: SparqlEndpoint, m: Model)(implicit request : Request[AnyContent]): Result = {
    val resultSize : Int = Try(request.queryString(Constants.COUNT_LABEL)).map(_.apply(0).toInt).filter(_ > 0).getOrElse(DEFAULT_SIZE)
    val self = ResourceFactory.createResource(uri)
    val selector: SimpleSelector = new SimpleSelector(null, p, null.asInstanceOf[RDFNode])
    val result: List[String] = ExtendedIteratorStream(m.listStatements(selector)).filter(s => s.getPredicate == p && s.getSubject != self) // get only pagerank triples
                                                                                 .sortBy(_.getLiteral.getFloat)(Ordering[Float].reverse) // sort them desc by page rank
                                                                                 .take(resultSize) // take first n reusults
                                                                                 .map(_.getSubject.toString) // get subject of the pagerank triple (this is an object of the requested resource)
                                                                                 .toList
    Ok(toJson(result))
  }

}