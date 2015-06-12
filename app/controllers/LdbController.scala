package controllers

import java.net.URI

import com.hp.hpl.jena.rdf.model.Model
import controllers.SearchSuggestionController._
import de.aksw.Constants
import de.aksw.sparql._
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.mvc.{Result, Action}

import scala.util.Try

/**
 * Created by dhaeb on 05.06.15.
 */
trait LdbController {

  def index = Action { request =>
    val uriTry: Try[String] = Try(request.queryString("uri")).map(se => se.apply(0))
    val endpoint: SparqlEndpoint = request.getQueryString("endpoint").map(e => new SparqlEndpoint(URI.create(e).toURL)).getOrElse(Constants.ENDPOINT_DBPEDIA)
    if (uriTry.isSuccess) {
      SparqlQueryCache.executeSparqlSubjectQuery(SparqlSubjectQueryRequest(endpoint.getURL.toString, uriTry.get)) match {
        case SparqlCacheResult(m: Model) => process(uriTry.get, endpoint, m)
        case SparqlConstructQueryResult(m : Model) => process(uriTry.get, endpoint, m)
        case SparqlQueryError(e) => BadRequest("There was an error during the SPARQL request!")
      }
    } else {
      BadRequest("You need to specify the uri parameter!")
    }
  }

  def process(uri : String, endpoint : SparqlEndpoint, m : Model) : Result

}

