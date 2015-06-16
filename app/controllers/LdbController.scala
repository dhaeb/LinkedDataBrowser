package controllers

import java.net.URI

import com.hp.hpl.jena.rdf.model._
import controllers.LocationsFromSubject._
import controllers.SearchSuggestionController._
import de.aksw.Constants
import de.aksw.sparql._
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.Logger
import play.api.libs.json.Json._
import play.api.libs.json.{JsString, JsNumber, Writes}
import play.api.mvc.{Result, Action}

import scala.util.Try

/**
 * Created by dhaeb on 05.06.15.
 */
trait LdbController {

  protected val logger: Logger = Logger(this.getClass())

  def index = Action { request =>
    val uriTry: Try[String] = Try(request.queryString("uri")).map(se => se.apply(0))
    val endpoint: SparqlEndpoint = request.getQueryString("endpoint").map(e => new SparqlEndpoint(URI.create(e).toURL)).getOrElse(Constants.ENDPOINT_DBPEDIA)
    if (uriTry.isSuccess) {
      logger.info(s"Handling uri ${uriTry} and endpoint ${endpoint.toString}...")
      SparqlQueryCache.executeSparqlSubjectQuery(SparqlSubjectQueryRequest(endpoint.getURL.toString, uriTry.get)) match {
        case SparqlCacheResult(m: Model) => process(uriTry.get, endpoint, m)
        case SparqlConstructQueryResult(m : Model) => process(uriTry.get, endpoint, m)
        case SparqlQueryError(e) => BadRequest("There was an error during the SPARQL request!")
      }
    } else {
      logger.debug("Bad request from ... ")
      BadRequest("You need to specify the uri parameter!")
    }
  }

  def process(uri : String, endpoint : SparqlEndpoint, m : Model) : Result

}

trait LdbRdfPropertySelectorController extends LdbController {
  def selectablePropertyUris : List[String]

  val selectablepProperties : List[Property] = selectablePropertyUris.map(ResourceFactory.createProperty(_))

  val F = classOf[java.lang.Float]

  implicit val literalWrites : Writes[Literal] = Writes[Literal](js => {
    Option(js.getDatatype).map(_.getJavaClass) match {
      case Some(F) => JsNumber(BigDecimal(js.getLexicalForm))
      case None => JsString(js.getLexicalForm)
    }
  })

  override def process(uri: String, endpoint: SparqlEndpoint, m: Model): Result = {
    val givenResource: Resource = ResourceFactory.createResource(uri)
    val tuples: List[(String, Literal)] = for {
      property <- selectablepProperties
    } yield {
        val key = property.getLocalName
        val value = Option(m.getProperty(givenResource, property)).map({ s =>
          s.getObject.asLiteral()
        }).getOrElse({
          logger.warn(s"No ${key} found for uri ${uri}")
          ResourceFactory.createPlainLiteral("")
        })
        (key, value)
      }
    val returnable = toJson(Map(tuples: _*))
    Ok(returnable)
  }

}

