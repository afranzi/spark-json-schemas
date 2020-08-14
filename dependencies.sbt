
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
  "org.apache.spark" %% "spark-core" % SparkVersion % Provided,             // https://spark.apache.org/
  "org.apache.spark" %% "spark-sql" % SparkVersion % Provided,              // https://spark.apache.org/sql/

  // - TESTS
  "org.scalatest" %% "scalatest" % "3.1.2" % Test  // http://scalatest.org/
)