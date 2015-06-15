package de.unileipzig.aksw

import de.aksw.Constants._
import de.aksw._
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.{JsNumber, JsValue, Json}
import play.api.test.Helpers._
import play.api.test._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class LocationsFromSubjectSpec extends Specification {

  "LocationsFromSubjectController" should {

    "request with valid uri should return proper long / lat" in new WithApplication {
      assume(isReachable(dbpediaHostname))
      val home = route(FakeRequest(GET, "/locations_from_subject?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2FLeipzig")).get
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "application/json")
      private val content = Json.parse(contentAsString(home))
      private val messageValueLong : JsValue = (content \ ("long"))
      private val messageValueLat : JsValue = (content \ ("lat"))
      println(messageValueLong)
      messageValueLong must equalTo(JsNumber(BigDecimal("12.383333206176757812")))
      messageValueLat must equalTo(JsNumber(BigDecimal("51.333332061767578125")))

    }

    "request without uri shoulud be a bad request" in new WithApplication {
      val home = route(FakeRequest(GET, "/locations_from_subject")).get
      status(home) must equalTo(BAD_REQUEST)
    }


  }

}
