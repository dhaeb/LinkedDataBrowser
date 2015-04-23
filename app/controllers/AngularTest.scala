package controllers

import play.api.mvc._
import play.twirl.api.{HtmlFormat, Html}

object AngularTest extends Controller {

  def index = Action {
    Ok(views.html.angular("Angular Test Page"))
  }

}