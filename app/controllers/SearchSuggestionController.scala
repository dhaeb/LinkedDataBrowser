package controllers

import java.io.File

import de.aksw.surface.SurfaceFormIndexer
import org.apache.lucene.document.Document
import play.api.libs.json.Json
import play.api.mvc._

import scala.util.Try


/**
 * Created by Dan Häberlein on 24.04.15.
 */
object SearchSuggestionController extends Controller {

  val LDB_INDEXABLE_PROPKEY: String = "ldb.dbpedia_surfaceforms"
  val LDB_INDEXDIR_PROPKEY: String = "ldb.dbpedia_indexdir"

  val query: String = "query"

  lazy val pathToTtl = System.getProperty(LDB_INDEXABLE_PROPKEY)
  lazy val indexDir = System.getProperty(LDB_INDEXDIR_PROPKEY)

  lazy val indexer : SurfaceFormIndexer = new SurfaceFormIndexer(new File(pathToTtl),new File(indexDir))

  def index = Action { request =>
    val labels: Try[String] = Try(request.queryString(query)).flatMap(labels => Try[String](labels(0)))
    if(labels.isSuccess){
      val label: String = labels.get
      def parseInfosFromDocment(d : Document) = {
        val labelName: String = "label"
        val uriName: String = "uri"
        Map((uriName -> d.getValues(uriName)(0)), (labelName -> d.getValues(labelName).filter(_.startsWith(label))(0)))
      }
      val transferable = indexer.query(label + "*").map(parseInfosFromDocment).toList
      Ok(Json.toJson(transferable))
    } else {
      BadRequest("you need to specify exactly one get paramter query!")
    }
  }

}