package controllers

import de.askw.model.generated.Tables
import de.askw.model.generated.Tables.{SusAnswerRow, AnswersRow}
import play.api.Logger
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import play.api.libs.json.Json.toJson
import play.api.libs.json._
import play.api.mvc._

import scala.collection.Set
import scala.util.Try

object SurveyController extends Controller {

  def list = DBAction { implicit rs =>
    def createDbQuery = {
      Tables.Questions.filter(!_.question.isEmpty)
    }
    val questionTuples: List[(Int, String)] = createDbQuery.run(rs.dbSession).toList.map(r => (r.id, r.question.get))
    Ok(views.html.survey_dialog(questionTuples))
  }

  val logger = Logger(this.getClass())

  def insertIntoDb = DBAction (play.api.mvc.BodyParsers.parse.json) { implicit request =>
    implicit val session = request.dbSession
    session.withTransaction {
      val jsObjectTry: Try[JsObject] = Try(request.body.asInstanceOf[JsObject]).map({jsObject =>
        if(jsObject.keys.exists(k => !k.startsWith("q"))){
          throw new IllegalArgumentException()
        }
        jsObject
      })
      if(jsObjectTry.isSuccess){
        logger.info("Evaluation data received...")
        val jsObject = jsObjectTry.get
        val id = Tables.SusAnswer.returning(Tables.SusAnswer.map(_.id)) insert SusAnswerRow(-1, Option("dummy"), Option("dummy"))
        jsObject.keys.foreach(s => {
          val questionId: Int = s.substring(1).toInt // delete leading q string (q1 == question with id 1 in db)
          val value: Byte = jsObject.value.get(s).get.toString.toByte // get vrating alue and convert to byte
          Tables.Answers += Tables.AnswersRow(questionId, id, value)
        })
        Ok(id.toString)
      } else {
        logger.info("Evaluation data received, but was not processable...")
        BadRequest("Your input data is not valid!")
      }
    }
  }

}