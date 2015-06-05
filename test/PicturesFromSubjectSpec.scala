package de.unileipzig.aksw

import de.aksw.Constants._
import de.aksw._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json.{JsArray, JsValue, Json}

import play.api.test._
import play.api.test.Helpers._

import scala.tools.nsc.interpreter.JList

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class PicturesFromSubjectSpec extends Specification {

  "PicturesFromSubject" should {

    val controllerResource: String = "/pictures_from_subject"

    "request json and return stubed json" in new WithApplication {
      assume(isReachable(dbpediaHostname))
      val home = route(FakeRequest(GET, s"${controllerResource}?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2FLeipzig")).get
      contentType(home) must beSome.which(_ == "application/json")
      status(home) must equalTo(OK)
      private val content : JsArray = Json.parse(contentAsString(home)).asInstanceOf[JsArray]
      content(0) toString() mustEqual ("\"http://commons.wikimedia.org/wiki/Special:FilePath/Flag_of_Leipzig.svg\"")
    }

    "request without uri shoulud be a bad request" in new WithApplication {
      val home = route(FakeRequest(GET, controllerResource)).get
      status(home) must equalTo(BAD_REQUEST)
    }

  }

}
