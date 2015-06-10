package de.aksw.sparql

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.hp.hpl.jena.rdf.model.Model
import de.aksw._
import org.apache.jena.riot.RDFDataMgr
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}
import de.aksw.Constants._
import scala.concurrent.duration._
import scala.util.Try

/**
 * Created by dhaeb on 01.06.15.
 */
class TestSparqlQueryExecutor(_system: ActorSystem) extends TestKit(_system) with FunSuiteLike with Matchers with BeforeAndAfterAll with ImplicitSender {
  private var model: Model = RDFDataMgr.loadModel(SIMPLESURFACEFORM_TURTLE_MODEL_FILENAME)

  def this() = this(ActorSystem("testactorsystem"))


  val request: SparqlSubjectQueryRequest = SparqlSubjectQueryRequest(DBPEDIA_ENDPOINT, SWAT_RESOURCE_URI)
  test("test sparql worker") {
    val testable = system.actorOf(Props[SparqlQueryExecutor])
    testable ! request
    expectMsgClass(3 seconds, classOf[Tuple2[String, Try[Model]]])
    ()
  }

  test("query string") {
    val fixture =
      s"""|
         |PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
         |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
         |PREFIX owl: <http://www.w3.org/2002/07/owl#>
         |
         |CONSTRUCT{ <test> ?p ?o}
         |WHERE {
         |    <test> ?p ?o .
         |    FILTER(!isLiteral(?o) || lang(?o) = "" || langMatches(lang(?o), "EN"))
         |    FILTER NOT EXISTS {
         |        <test> rdf:type ?o .
         |        ?o rdfs:subClassOf ?directType .
         |        FILTER NOT EXISTS {
         |            ?o owl:equivalentClass ?directType .
         |        }
         |    }
         |}""".stripMargin
    assert(fixture === SparqlSubjectQueryRequest.querystring("test"))
  }

  test("test cache") {
    assume(isReachable(dbpediaHostname))
    val testable = system.actorOf(Props[SparqlQueryCache])
    testable ! request
    testable ! request
    testable ! request
    expectMsgClass(3 seconds, classOf[Try[Model]])
    expectMsgClass(1 millisecond, classOf[Try[Model]])
    expectMsgClass(1 millisecond, classOf[Try[Model]])
    testable ! request
    expectMsgClass(10 milliseconds, classOf[Try[Model]])
    ()
  }

  test("test standalone mode") {
    val result = SparqlQueryCache.executeSparqlSubjectQuery(SparqlSubjectQueryRequest(DBPEDIA_ENDPOINT, SWAT_RESOURCE_URI))
    if (result.isSuccess) {
      assert(!result.get.isEmpty)
    }
  }

  override def afterAll: Unit = TestKit.shutdownActorSystem(system)

}
