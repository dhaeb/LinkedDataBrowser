package controllers

import play.api.mvc._
import play.twirl.api.{HtmlFormat, Html}

/**
 * Created by bugge on 10.05.15.
 */
object Result extends Controller {

  def index = Action {
    Ok(views.html.result("Linked Data Browser"))
  }
}
