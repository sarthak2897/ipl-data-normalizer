import play.core.PlayVersion.akkaVersion

name := """play_basics"""
organization := "com.play"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

//scalaVersion := "2.12.5"

//val akkaVersion = "2.5.11"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test

lazy val akkaDependencies = Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "0.8",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.22",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
)

lazy val loggingDependencies = Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
//  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)

lazy val slickDependencies = Seq(
  "mysql" % "mysql-connector-java" % "8.0.27",
  "com.typesafe.play" %% "play-slick" % "5.0.0",
)

lazy val otherDependencies = Seq(
  "io.spray" %% "spray-json" % "1.3.5"
)

libraryDependencies ++= (akkaDependencies ++ loggingDependencies ++
  slickDependencies ++ otherDependencies)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.play.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.play.binders._"
