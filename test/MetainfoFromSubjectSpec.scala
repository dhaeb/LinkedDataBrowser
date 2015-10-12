package de.unileipzig.aksw

import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import de.aksw._
import de.aksw.Constants._
/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class MetainfoFromSubjectSpec extends Specification {

  "MetainfoFromSubjectController" should {

    "request with valid uri should return the rdfs comment section" in new WithApplication {
      assume(isReachable(dbpediaHostname))
      val home = route(FakeRequest(GET, "/metainfo_from_subject?uri=http%3A%2F%2Fdbpedia.org%2Fresource%2FLeipzig")).get
      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "application/json")
      private val content = Json.parse(contentAsString(home))
      private val messageValue: String = (content \ ("comment")).toString().replace("\"", "")
      messageValue must startWith("Leipzig (/ˈlaɪptsɪɡ/; German pronunciation: [ˈlaɪ̯pt͡sɪç]")
    }

    "request without uri shoulud be a bad request" in new WithApplication {
      val home = route(FakeRequest(GET, "/metainfo_from_subject")).get
      status(home) must equalTo(BAD_REQUEST)
    }

  }
}
