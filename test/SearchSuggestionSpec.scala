package de.unileipzig.aksw

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.{JsArray, Json}
import play.api.test.Helpers._
import play.api.test._
import java.io.File
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
      val home = route(FakeRequest(GET, "/searchsuggestion?query=java")).get
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "application/json")
      private val content = Json.parse(contentAsString(home)).asInstanceOf[JsArray]
      assert(4 === content.value.length)
    }

    "answer requests with BadRequest when not sending query get parameter" in new WithApplication {
      val home = route(FakeRequest(GET, "/searchsuggestion")).get
      status(home) must equalTo(BAD_REQUEST)
    }
  }

}
