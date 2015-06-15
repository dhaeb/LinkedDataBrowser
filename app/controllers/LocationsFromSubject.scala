package controllers

object LocationsFromSubject extends LdbRdfPropertySelectorController {

  override def selectablePropertyUris: List[String] = List("http://www.w3.org/2003/01/geo/wgs84_pos#lat" , "http://www.w3.org/2003/01/geo/wgs84_pos#long")
}