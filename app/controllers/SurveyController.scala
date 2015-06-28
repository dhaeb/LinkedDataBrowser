package controllers

import java.sql.Timestamp

import de.askw.model.generated.Tables
import de.askw.model.generated.Tables.SusAnswerRow
import org.joda.time.{DateTime, Minutes}
import play.api.Logger
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._

import scala.util.{Success, Try}

object SurveyController extends Controller {

  def list = DBAction { implicit rs =>
    def createDbQuery = {
      Tables.Questions.filter(!_.question.isEmpty)
    }
    val questionTuples: List[(Int, String)] = createDbQuery.run(rs.dbSession).toList.map(r => (r.id, r.question.get))
    Ok(views.html.survey_dialog(questionTuples))
  }

  val logger = Logger(this.getClass())

  def now: java.util.Date = new java.util.Date();

  def insertIntoDb = DBAction(play.api.mvc.BodyParsers.parse.json) {
    implicit request =>
      implicit val session = request.dbSession
      def handleSuccessfulRequest(jsObject: JsObject, remoteAddress: String): SimpleResult = {
        logger.info("Evaluation data received...")
        val comment: Option[String] = jsObject.value.get("comment").map({ s =>
          val string: String = s.toString()
          string.substring(1, string.length - 1)
        })

        val insertable: Tables.SusAnswerRow = SusAnswerRow(-1, comment, Some(remoteAddress), new Timestamp(now.getTime()))
        val id = Tables.SusAnswer.returning(Tables.SusAnswer.map(_.id)) insert insertable
        jsObject.keys.filter(k => k.startsWith("q")).foreach(s => {
          val questionId: Int = s.substring(1).toInt // delete leading q string (q1 == question with id 1 in db)
          val value: Byte = jsObject.value.get(s).get.toString.toByte // get vrating alue and convert to byte
          Tables.Answers += Tables.AnswersRow(questionId, id, value)
        })
        Ok(id.toString)
      }

      def checkInputJsonData: Try[JsObject] = {
        val jsObjectTry: Try[JsObject] = Try(request.body.asInstanceOf[JsObject]).map({ jsObject =>
          if (jsObject.keys.exists(k => !(k.matches("q\\d+") && jsObject.value(k).isInstanceOf[JsNumber] || k.equals("comment")))) {
            throw new IllegalArgumentException()
          }
          jsObject
        })
        jsObjectTry
      }

      session.withTransaction {
        checkInputJsonData match {
          case Success(jsObject) => {
            val remoteAddress = request.remoteAddress
            val alreadyInsertedRowsFromThatIp: Seq[Tables.SusAnswerRow] = Tables.SusAnswer.filter(r => r.ip === remoteAddress).sortBy(_.ts.desc).run
            if (alreadyInsertedRowsFromThatIp.isEmpty) {
              handleSuccessfulRequest(jsObject, remoteAddress)
            } else {
              val row = alreadyInsertedRowsFromThatIp.apply(0)
              val between = Minutes.minutesBetween(new DateTime(row.ts.getTime), new DateTime(now.getTime))
              val minutes: Int = between.getMinutes
              if (minutes > 3) {
                handleSuccessfulRequest(jsObject, remoteAddress)
              } else {
                logger.info(s"Evaluation data not insertable, request from ip ${remoteAddress}: this user is suspected to spam evals (${minutes} minutes)")
                BadRequest("Only one evaluation in short duration permitted!")
              }
            }
          }
          case _ => {
            logger.info("Evaluation data received, but was not processable...")
            BadRequest("Your input data is not valid!")
          }
        }
      }
  }

}