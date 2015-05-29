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

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4"

lazy val build_deps = taskKey[Unit]("Execute maven build of SemWeb2NL")

build_deps := {
    val mavenCmd = List("mvn", "install", "-fn")
    val cmd = System.getProperty("os.name") match {
        case e if e.startsWith("Windows") => List("cmd", "/c") ++ mavenCmd
        case _ => mavenCmd
    }
    sys.process.Process(cmd, new java.io.File("opt/SemWeb2NL")) !
}

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  