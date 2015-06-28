package de.unileipzig.aksw

import controllers.SearchSuggestionController
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.test.Helpers._
import play.api.test._
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class SearchSuggestionSpec extends Specification {

  import controllers.SearchSuggestionController._

  "SearchSuggestion" should {

    "request get parameter and return a list of uris" in new WithApplication {
      val queryString = "java"
      val home = route(FakeRequest(GET, s"/searchsuggestion?query=${queryString}")).get
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "application/json")
      private val content = Json.parse(contentAsString(home)).asInstanceOf[JsObject]
      private val resultSuggestions: Option[JsArray] = content.value.get(SearchSuggestionController.SEARCHSUGGESTIONS_LABEL).map(_.asInstanceOf[JsArray])
      assert(resultSuggestions.isDefined, "There are no search suggestions!")
      assert(3 === resultSuggestions.get.value.length) // there are four label entries containing java, three of them are unique
      assert(queryString == (content \ SearchSuggestionController.QUERY_LABEL).as[String] )
    }

    "answer requests with BadRequest when not sending query get parameter" in new WithApplication {
      val home = route(FakeRequest(GET, "/searchsuggestion")).get
      status(home) must equalTo(BAD_REQUEST)
    }
  }

}
