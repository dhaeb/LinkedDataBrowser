name := "linked_data_browser"

version := "1.0"

lazy val `linked_data_browser` = (project in file(".")).enablePlugins(PlayScala)

//for sbt play bower plugin https://github.com/dwickern/sbt-bower
JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

scalaVersion := "2.11.1"

resolvers += Resolver.mavenLocal

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

// dependencies

// compile scope

libraryDependencies ++= Seq(
    "org.aksw.semweb2nl" % "triple2nl" % "0.0.1-SNAPSHOT"
    exclude ("org.apache.xmlbeans", "xmlbeans")
    exclude ("xml-apis", "xml-apis")
    exclude ("simplenlg", "simplenlg") // because simplenlg-4.4.3.jar brings an outdated implementation of org.junit.runner.Description
)

// scope test

libraryDependencies +=   "com.gilt" %% "lib-lucene-sugar" % "0.2.3"

libraryDependencies += "org.apache.jena" % "apache-jena-libs" % "2.13.0"

libraryDependencies +=  "org.scalatest" %% "scalatest" % "3.0.0-SNAP4"

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )   	
