name := "BusinessLogic"

version := "0.1"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.2",
  "org.json4s" %% "json4s-jackson" % "3.7.0-M7",
  "org.json4s" %% "json4s-native" % "3.7.0-M7")

libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.9.0"
