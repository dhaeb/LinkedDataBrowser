package de.unileipzig.aksw

import de.aksw.Constants._
import de.aksw._
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.json.{JsArray, JsValue, Json}

import play.api.test._
import play.api.test.Helpers._
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class RdfFromSubjectSpec extends Specification {

  "RdfFromSubjectSpec" should {

    "handle a request to get high ranked wikipedia triples" in new WithApplication{
      assume(isReachable(dbpediaHostname))
      private val request: JsArray = execRequest("/rdf_from_subject?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2FLeipzig")
      request.value.size must equalTo(10)
    }

    "handle a request with bounded size" in new WithApplication{
      assume(isReachable(dbpediaHostname))
      val size = 3
      private val request: JsArray = execRequest(s"/rdf_from_subject?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2FLeipzig&${COUNT_LABEL}=${size}")
      request.value.size must equalTo(size)
    }

    def execRequest(queryString : String) : JsArray = {
      val home = route(FakeRequest(GET, queryString)).get
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "application/json")
      Json.parse(contentAsString(home)).asInstanceOf[JsArray]
    }

    "request without uri shoulud be a bad request" in new WithApplication {
      val home = route(FakeRequest(GET, "/rdf_from_subject")).get
      status(home) must equalTo(BAD_REQUEST)
    }

  }

}
