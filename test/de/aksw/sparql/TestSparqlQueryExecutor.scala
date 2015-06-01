package de.aksw.sparql

import akka.actor.{Props, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.hp.hpl.jena.query.QueryFactory
import com.hp.hpl.jena.rdf.model.Model
import org.apache.jena.riot.RDFDataMgr
import org.scalatest.{BeforeAndAfterAll, Matchers, FunSuiteLike}
import de.aksw._
import scala.collection.mutable
import scala.concurrent.duration._

import scala.util.{Try, Success}

/**
 * Created by dhaeb on 01.06.15.
 */
class TestSparqlQueryExecutor(_system: ActorSystem) extends TestKit(_system) with FunSuiteLike with Matchers with BeforeAndAfterAll with ImplicitSender {
  private var model: Model = RDFDataMgr.loadModel(SIMPLESURFACEFORM_TURTLE_MODEL_FILENAME)

  def this() = this(ActorSystem("testactorsystem"))

  val request: SparqlSubjectQueryRequest = SparqlSubjectQueryRequest(s"http://${dbpediaHostname}/sparql", "http://dbpedia.org/resource/SWAT")

  test("test sparql worker"){
    val testable = system.actorOf(Props[SparqlQueryExecutor])
    testable ! request
    expectMsgClass(1 seconds, classOf[Tuple2[String, Try[Model]]])
    ()
  }

  test("query string"){
    assert("CONSTRUCT{ <test> ?p ?o} \nWHERE {\n<test> ?p ?o .\n}" === SparqlSubjectQueryRequest.querystring("test"))
  }

  test("test cache"){
    assume(isReachable(dbpediaHostname))
    val testable = system.actorOf(Props[SparqlQueryCache])
    testable ! request
    testable ! request
    testable ! request

    var messages : Seq[Try[Model]]= Seq[Try[Model]]()
    expectMsgClass(1 seconds, classOf[Try[Model]])
    expectMsgClass(1 millisecond, classOf[Try[Model]])
    expectMsgClass(1 millisecond, classOf[Try[Model]])
    testable ! request
    expectMsgClass(1 millisecond, classOf[Try[Model]])
    ()
  }

  override def afterAll: Unit = TestKit.shutdownActorSystem(system)

}
