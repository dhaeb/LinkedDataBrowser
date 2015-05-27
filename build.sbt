name := "linked_data_browser"

version := "1.0"

lazy val `linked_data_browser` = (project in file(".")).enablePlugins(PlayScala)

//for sbt play bower plugin https://github.com/dwickern/sbt-bower
JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )		

libraryDependencies +=   "com.gilt" %% "lib-lucene-sugar" % "0.2.3"

libraryDependencies += "org.apache.jena" % "apache-jena-libs" % "2.13.0"

libraryDependencies +=  "org.scalatest" %% "scalatest" % "3.0.0-SNAP4"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )   	