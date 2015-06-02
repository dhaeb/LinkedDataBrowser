package de.aksw

import org.dllearner.kb.sparql.SparqlEndpoint

/**
 * Created by dhaeb on 02.06.15.
 */
object Constants {
  val dbpediaHostname: String = "dbpedia.org"
  val DBPEDIA_ENDPOINT: String = s"http://${dbpediaHostname}/sparql"
  val ENDPOINT_DBPEDIA: SparqlEndpoint = SparqlEndpoint.getEndpointDBpedia

}
