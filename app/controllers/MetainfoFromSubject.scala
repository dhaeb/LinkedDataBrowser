package controllers

import com.hp.hpl.jena.rdf.model._
import controllers.SearchSuggestionController._
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Result, Action}

object MetainfoFromSubject extends LdbController {

  val rdfsComment: Property = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#", "comment")

  override def process(uri: String, endpoint: SparqlEndpoint, m: Model): Result = {
    val givenResource: Resource = ResourceFactory.createResource(uri)
    val returnable: String = Option(m.getProperty(givenResource, rdfsComment)).map(_.getLiteral.getLexicalForm).getOrElse({
      logger.warn(s"No abstract found for uri ${uri}")
        ""
    })
    Ok(toJson(Map("abstract" -> returnable)))
  }
}