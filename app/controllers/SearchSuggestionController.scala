package controllers

import play.api._
import play.api.libs.json.Json
import play.api.mvc._


/**
 * Created by Dan Häberlein on 24.04.15.
 */
object SearchSuggestionController extends Controller {
  def index = Action(play.api.mvc.BodyParsers.parse.json) { request =>
    val transferable: Map[String, String] = Map("message" -> "This service is under construction")
    Ok(Json.toJson(transferable))
  }
}