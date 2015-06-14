package controllers

import controllers.SearchSuggestionController._
import play.api.libs.json.Json
import play.api.libs.ws.{WSResponse, WS}
import play.api.mvc.{Result, Action}
import play.mvc.Controller

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by dhaeb on 13.06.15.
 */
object FoxProxy extends Controller {

  def index = Action.async(play.api.mvc.BodyParsers.parse.json) { request =>
    WS.url("http://139.18.2.164:4444/api")
      .post(request.body)
      .map({ response =>
        val contentType = response.header("Content-Type").getOrElse("text/plain")
        Status(response.status)(response.body).as(contentType)
    })
  }
}