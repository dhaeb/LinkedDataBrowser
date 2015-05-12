name := "linked_data_browser"

version := "1.0"

lazy val `linked_data_browser` = (project in file(".")).enablePlugins(PlayScala)

JsEngineKeys.engineType := JsEngineKeys.EngineType.Node

scalaVersion := "2.11.1"

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  