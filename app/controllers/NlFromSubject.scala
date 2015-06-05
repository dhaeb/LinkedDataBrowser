package controllers

import java.util

import com.hp.hpl.jena.graph.Triple
import com.hp.hpl.jena.rdf.model.Model
import controllers.SearchSuggestionController._
import de.aksw.iterator.ExtendedIteratorStream
import org.aksw.triple2nl.TripleConverter
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.libs.json.Json._
import play.api.mvc.Result

import scala.collection.JavaConverters._

object NlFromSubject extends LdbController {
  override def process(uri: String, endpoint: SparqlEndpoint, m: Model): Result = {
    val triples: util.List[Triple] = ExtendedIteratorStream(m.listStatements()).map({ s =>
      Triple.create(s.getSubject.asNode(), s.getPredicate.asNode(), s.getObject.asNode())
    }).toList.asJava
    val text: String = new TripleConverter(endpoint, "triple2nl_cache", "public/wordnet/linux/dict").convertTriplesToText(triples)
    Ok(toJson(Map("nl" -> text)))
  }
}