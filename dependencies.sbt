
organization := "com.afranzi"
name := "spark.jsonschemas"
description := "Create Spark Schemas from JSON-Schemas"
homepage := Some(url("https://github.com/afranzi/spark-json-schemas"))
scalaVersion := "2.12.10"
startYear := Some(2020)

javacOptions ++= List("-source", "1.8", "-target", "1.8")

resolvers += "jitpack" at "https://jitpack.io"
resolvers += Resolver.mavenCentral

val SparkVersion = "3.0.0"

libraryDependencies ++= Seq(
  "com.github.everit-org.json-schema" % "org.everit.json.schema" % "1.12.1", // https://github.com/everit-org/json-schema

  // - SPARK
  "org.apache.spark" %% "spark-core" % SparkVersion,             // https://spark.apache.org/
  "org.apache.spark" %% "spark-sql" % SparkVersion,              // https://spark.apache.org/sql/

//  // - LOGGERS
  "org.clapper" %% "grizzled-slf4j" % "1.3.4" exclude("org.slf4j", "slf4j-api"), // http://software.clapper.org/grizzled-slf4j/
  "ch.qos.logback" % "logback-classic" % "1.2.3",                                // https://logback.qos.ch/

  // - TESTS
  "org.scalatest" %% "scalatest" % "3.2.0" % Test  // http://scalatest.org/
)