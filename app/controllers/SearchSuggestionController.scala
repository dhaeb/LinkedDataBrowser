package controllers

import java.io.File

import de.aksw.surface.SurfaceFormIndexer
import org.apache.lucene.document.Document
import play.api.Play
import play.api.libs.json.Json._
import play.api.mvc._
import de.aksw.Constants._

import scala.util._


/**
 * Created by Dan Häberlein on 24.04.15.
 */
object SearchSuggestionController extends Controller {

  val LDB_INDEXABLE_PROPKEY: String = "ldb.dbpedia_surfaceforms"
  val LDB_INDEXDIR_PROPKEY: String = "ldb.dbpedia_indexdir"

  val DEFAULT_RESULT_COUNT: String = "10"

  val QUERY_LABEL : String = "query"

  lazy val pathToTtl = Play.current.configuration.getString(LDB_INDEXABLE_PROPKEY).getOrElse("")
  lazy val indexDir = Play.current.configuration.getString(LDB_INDEXDIR_PROPKEY).getOrElse("index")

  lazy val indexer : SurfaceFormIndexer = new SurfaceFormIndexer(new File(pathToTtl),new File(indexDir))

  def index = Action { request =>
    val labelsTry: Try[Seq[String]] = Try(request.queryString(QUERY_LABEL))
    val countParamAsString : String = request.getQueryString(COUNT_LABEL).getOrElse(DEFAULT_RESULT_COUNT)
    def count = Try(countParamAsString).map(s => s.toInt) match {
      case Success(i) => i
      case Failure(_) => DEFAULT_RESULT_COUNT.toInt
    }
    if(labelsTry.isSuccess){
      handleSuccessfulRequest(labelsTry.get, count)
    } else {
      BadRequest("You need to specify exactly one get paramter query!")
    }
  }

  def handleSuccessfulRequest(labels: Seq[String], count: Int) = {
    val transferable = indexer.query(labels.mkString(" "), count).map(parseInfosFromDocment).toList
    Ok(toJson(transferable))
  }

  def parseInfosFromDocment(d: Document) = {
    val uriName: String = "uri"
    val uriValue = toJson(d.getValues(uriName)(0))
    val labelName: String = "label"
    val labelValue = toJson(d.getValues(labelName)(0))
    val lastPartOfUriName: String = "uriName"
    val lastPartOfUriValue = toJson(d.getValues(lastPartOfUriName)(0))

    toJson(Map(uriName -> uriValue, labelName -> labelValue, lastPartOfUriName -> lastPartOfUriValue))
  }
}