package controllers

import com.hp.hpl.jena.rdf.model._
import controllers.MetainfoFromSubject._
import controllers.SearchSuggestionController._
import org.dllearner.kb.sparql.SparqlEndpoint
import play.api.libs.json.{JsNumber, JsString, Writes, Json}
import play.api.libs.json.Json._
import play.api.mvc.{Result, Action}

object LocationsFromSubject extends LdbController {

  val selectablepPropertiesUris : List[String] = List("http://www.w3.org/2003/01/geo/wgs84_pos#lat" , "http://www.w3.org/2003/01/geo/wgs84_pos#long")

  val selectablepProperties : List[Property] = selectablepPropertiesUris.map(ResourceFactory.createProperty(_))

  val F = classOf[java.lang.Float]

  implicit val literalWrites : Writes[Literal] = Writes[Literal](js => {
    js.getDatatype.getJavaClass  match {
      case F => JsNumber(BigDecimal(js.getLexicalForm))
      case _ => JsString(js.getLexicalForm)
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