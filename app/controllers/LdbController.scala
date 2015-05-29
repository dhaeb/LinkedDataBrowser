package controllers

import play.api.mvc._

object LdbController extends Controller {

  def index = Action {
    Ok(views.html.ldb_harness("Linked Data Browser"))
  }

}