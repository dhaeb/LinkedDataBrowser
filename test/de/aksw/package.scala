package de

import java.io.{IOException, File}
import java.net.InetAddress

/**
 * Created by dhaeb on 28.05.15.
 */
package object aksw {

  val dbpediaHostname: String = "dbpedia.org"
  val DBPEDIA_ENDPOINT: String = s"http://${dbpediaHostname}/sparql"
  val SWAT_RESOURCE_URI: String = "http://dbpedia.org/resource/SWAT"
  val SIMPLESURFACEFORM_TURTLE_MODEL_FILENAME : String = "test-surface-forms.ttl"

  def deleteRecursively(f: File): Unit = {
    if (f.exists()) {
      if (f.isDirectory()) {
        f.listFiles().foreach {
          deleteRecursively(_)
        }
      }
      f.delete()
    }
  }

  @throws(classOf[IOException])
  def isReachable(dbpediaHostname: String): Boolean = {
    var returnable: Boolean = false
    try {
      returnable = !InetAddress.getByName(dbpediaHostname).isAnyLocalAddress
    }
    catch {
      case e: IOException => {
      }
    }
    return returnable
  }
}
