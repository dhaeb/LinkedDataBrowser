package controllers

import com.hp.hpl.jena.rdf.model.{Property, RDFNode, Model}
import controllers.SearchSuggestionController._
import de.aksw.iterator.ExtendedIteratorStream
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.libs.json.Json
import play.api.mvc.{Result, Action}

object PicturesFromSubject extends LdbController {
  override def process(uri: String, endpoint: SparqlEndpoint, m: Model): Result = {
    val foafDepiction: Property = m.createProperty("http://xmlns.com/foaf/0.1/depiction")
    val stream: Stream[RDFNode] = ExtendedIteratorStream.apply(m.listObjectsOfProperty(foafDepiction))
    Ok(Json.toJson(stream.map(_ toString)))
  }
}