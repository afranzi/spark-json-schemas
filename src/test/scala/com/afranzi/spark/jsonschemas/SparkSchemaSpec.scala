package com.afranzi.spark.jsonschemas

import com.afranzi.spark.jsonschemas.SparkSchema.SparkSchemaExtractor
import com.afranzi.spark.jsonschemas.clients.ResourceSchemaClient
import org.apache.spark.sql.types.{StructField, _}
import org.everit.json.schema._

class SparkSchemaSpec extends UnitSpec {

  val schemaBuilder: SchemaBuilder = SchemaBuilder(Some(new ResourceSchemaClient))

  def getSparkSchema(schemaPath: String): StructType = schemaBuilder.loadSchema(schemaPath).sparkSchema

  "Spark schemas" should "be create using json-schemas" in {
    val schemaPath = "/schemas/events/base-event/1.json"
    val sparkSchema = getSparkSchema(schemaPath)

    val expected = StructType(Seq(
      StructField("createdAt", TimestampType, nullable = true),
      StructField("deploymentEnv", StringType, nullable = true),
      StructField("product", StructType(Seq(
        StructField("id", StringType, nullable = true),
        StructField("name", StringType, nullable = true),
        StructField("version", StringType, nullable = true)
      )), nullable = true),
      StructField("schema", StringType, nullable = true),
      StructField("source", StringType, nullable = true),
      StructField("user", StructType(Seq(
        StructField("id", StringType, nullable = true)
      )), nullable = true)
    ))

    sparkSchema shouldBe expected
  }

  "Complex schema" should "provide all schemas" in {
    val schemaPath = "/schemas/events/analytics-device-event/0.json"
    val sparkSchema = getSparkSchema(schemaPath)

    val expected = StructType(Seq(
      StructField("anonymousId", StringType, nullable = true),
      StructField("createdAt", TimestampType, nullable = true),
      StructField("deploymentEnv", StringType, nullable = true),
      StructField("device", StructType(Seq(
        StructField("id", StringType, nullable = true),
        StructField("model", StringType, nullable = true)
      )), nullable = true),
      StructField("element", StringType, nullable = true),
      StructField("event", StringType, nullable = true),
      StructField("product", StructType(Seq(
        StructField("id", StringType, nullable = true),
        StructField("name", StringType, nullable = true),
        StructField("version", StringType, nullable = true)
      )), nullable = true),
      StructField("schema", StringType, nullable = true),
      StructField("source", StringType, nullable = true),
      StructField("timestamp", FloatType, nullable = true),
      StructField("unique_name", StringType, nullable = true),
      StructField("user", StructType(Seq(
        StructField("id", StringType, nullable = true)
      )), nullable = true),
      StructField("userId", StringType, nullable = true),
      StructField("view", StringType, nullable = true)
    ))

    sparkSchema shouldBe expected
  }

  "Schema with references" should "work" in {
    val schemaContent =
      """{
          "properties": {
              "foo": {"type": "integer"},
              "bar": {"$ref": "#/properties/foo"}
          }
      }"""

    val schemaJson = JsonReader.readFromContent(schemaContent)
    val schema: Schema = schemaBuilder.buildSchema(schemaJson)
    val sparkSchema = schema.sparkSchema

    val expected = StructType(Seq(
      StructField("bar", IntegerType, nullable = true),
      StructField("foo", IntegerType, nullable = true)
    ))

    sparkSchema shouldBe expected
  }

  "Zalando schema" should "build the proper Spark schema" in {
    val schemaPath = "/zalando/test2.json"
    val sparkSchema = getSparkSchema(schemaPath)

    val expected = StructType(Seq(
      StructField("address", StructType(Seq(
        StructField("zip", StringType, nullable = true)
      )), nullable = true),
      StructField("name", StringType, nullable = true)
    ))

    sparkSchema shouldBe expected
  }

  "Zalando Schema" should "work with internal references" in {
    val schemaPath = "/zalando/testReferencesSchema.json"
    val sparkSchema = getSparkSchema(schemaPath)

    val expected = StructType(Seq(
      StructField("addressA", StructType(Seq(
        StructField("zip", StringType, nullable = true)
      )), nullable = true),
      StructField("addressB", StructType(Seq(
        StructField("zip", StringType, nullable = true)
      )), nullable = true),
      StructField("name", StringType, nullable = true)
    ))

    sparkSchema shouldBe expected
  }

}
