name := "linked_data_browser"

version := "1.0"

lazy val `linked_data_browser` = (project in file(".")).enablePlugins(PlayScala)

//for sbt play bower plugin https://github.com/dwickern/sbt-bower
JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

scalaVersion := "2.11.1"

resolvers += Resolver.mavenLocal

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

resolvers += "aksw release" at "http://maven.aksw.org/repository/internal/"

resolvers += "aksw snapshot" at "http://maven.aksw.org/repository/snapshots/"

resolvers += "dev.davidsoergel.com releases" at "http://dev.davidsoergel.com/nexus/content/repositories/releases"

resolvers += "dev.davidsoergel.com snapshots" at "http://dev.davidsoergel.com/nexus/content/repositories/snapshots"

// dependencies

// compile scope
libraryDependencies +=   "com.gilt" %% "lib-lucene-sugar" % "0.2.3"

libraryDependencies += "org.apache.jena" % "apache-jena-libs" % "2.13.0"

libraryDependencies ++= Seq(
"org.aksw.semweb2nl" % "triple2nl" % "0.0.1-SNAPSHOT"
    exclude ("org.apache.xmlbeans", "xmlbeans")
    exclude ("xml-apis", "xml-apis")
    exclude("com.martiansoftware", "JSAP")
    exclude("org.aksw", "semlibsvm")
    exclude("org.apache.jena","jena-arq")
)

libraryDependencies ++= Seq(
    javaWs
)

dependencyOverrides += "org.apache.httpcomponents" % "httpclient" % "4.2.6"

libraryDependencies += "com.martiansoftware" % "jsap" % "2.1"

libraryDependencies +=  "com.typesafe.akka" %% "akka-actor" % "2.3.11"

// scope test

libraryDependencies +=  "org.scalatest" %% "scalatest" % "3.0.0-SNAP4" % "test"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "0.8.1"

libraryDependencies += "com.typesafe.slick" % "slick-codegen_2.11" % "2.1.0"

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.24"


unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )
