package controllers

import java.io.File

import de.aksw.surface.SurfaceFormIndexer
import org.apache.lucene.document.Document
import play.api.libs.json.Json._
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
    val labelsTry: Try[Seq[String]] = Try(request.queryString(query))
    if(labelsTry.isSuccess){
      val labels: Seq[String] = labelsTry.get
      def parseInfosFromDocment(d : Document) = {
        val uriName: String = "uri"
        val uriValue = toJson(d.getValues(uriName)(0))
        val labelName: String = "label"
        val labelValue = toJson(d.getValues(labelName)(0))
        val lastPartOfUriName : String = "uriName"
        val lastPartOfUriValue = toJson(d.getValues(lastPartOfUriName)(0))
        toJson(Map(uriName -> uriValue, labelName -> labelValue, lastPartOfUriName -> lastPartOfUriValue))
      }
      val transferable = indexer.query(labels.mkString(" ")).map(parseInfosFromDocment).toList
      Ok(toJson(transferable))
    } else {
      BadRequest("You need to specify exactly one get paramter query!")
    }
  }

}