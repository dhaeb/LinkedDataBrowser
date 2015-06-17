package de.aksw.sparql

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.hp.hpl.jena.rdf.model.Model
import de.aksw.Constants._
import de.aksw._
import org.apache.jena.riot.RDFDataMgr
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike, Matchers}

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
         |CONSTRUCT{ <test> ?p ?o .
         |           ?o <http://dbpedia.org/ontology/wikiPageRank> ?pagerank}
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
         |    OPTIONAL {
         |      ?o <http://dbpedia.org/ontology/wikiPageRank> ?pagerank
         |    }
         |}""".stripMargin
    assert(fixture === SparqlSubjectQueryRequest.querystring("test"))
  }

  test("test cache") {
    assume(isReachable(dbpediaHostname))
    val testable = createTestableActor()
    testable ! request
    testable ! request
    testable ! request
    expectMsgClass(3 seconds, classOf[SparqlConstructQueryResult])
    expectMsgClass(10 millisecond, classOf[SparqlConstructQueryResult])
    expectMsgClass(10 millisecond, classOf[SparqlConstructQueryResult])
    Thread.sleep(10L)
    testable ! request
    expectMsgClass(10 milliseconds, classOf[SparqlCacheResult])
    ()
  }

  def createTestableActor(p : Props = SparqlQueryCache.createProps()): ActorRef = {
    system.actorOf(p)
  }

  test("test standalone mode") {
    assume(isReachable(dbpediaHostname))
    val result = SparqlQueryCache.executeSparqlSubjectQuery(SparqlSubjectQueryRequest(DBPEDIA_ENDPOINT, SWAT_RESOURCE_URI))
    assert(result.isInstanceOf[SparqlConstructQueryResult])
    val result2 = SparqlQueryCache.executeSparqlSubjectQuery(SparqlSubjectQueryRequest(DBPEDIA_ENDPOINT, SWAT_RESOURCE_URI))
    assert(result2.isInstanceOf[SparqlCacheResult])
  }

  test("test cache ttl") {
    assume(isReachable(dbpediaHostname))
    val ttl: FiniteDuration = 100 milliseconds
    val testable = createTestableActor(SparqlQueryCache.createProps(ttl, ttl))
    testable ! request
    expectMsgClass(3 seconds, classOf[SparqlConstructQueryResult])
    testable ! request
    expectMsgClass(10 milliseconds, classOf[SparqlCacheResult])
    Thread.sleep(ttl.toMillis + 50L)
    testable ! CheckTtlInCache
    testable ! request
    expectMsgClass(3 seconds, classOf[SparqlConstructQueryResult])
    ()
  }

  override def afterAll: Unit = TestKit.shutdownActorSystem(system)

}
