package controllers

import com.hp.hpl.jena.rdf.model._
import controllers.SearchSuggestionController._
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.{Result, Action}

object MetainfoFromSubject extends LdbRdfPropertySelectorController {

  override def selectablePropertyUris: List[String] = "http://www.w3.org/2000/01/rdf-schema#comment" :: Nil
}