package de.unileipzig.aksw

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json.{JsValue, Json}

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class NlFromSubjectSpec extends Specification {

  "RdfToNl" should {

    "request with valid uri should return proper NL" in new WithApplication {
      val home = route(FakeRequest(GET, "/nl_from_subject?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2FRDF_Schema")).get
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "application/json")
      private val content = Json.parse(contentAsString(home))
      private val messageValue: String = (content \ ("nl")).toString().replace("\"", "")
      messageValue must contain ("RDF Schema is a world wide web consortium standard")
    }
  }

}
