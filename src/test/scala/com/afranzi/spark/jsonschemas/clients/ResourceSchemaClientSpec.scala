package com.afranzi.spark.jsonschemas.clients

import com.afranzi.spark.jsonschemas.Exceptions.SchemaNotFoundException
import com.afranzi.spark.jsonschemas.{JsonReader, UnitSpec}
import org.json.JSONObject

import scala.io.Source

class ResourceSchemaClientSpec extends UnitSpec {

  val resourceSchemaClient = new ResourceSchemaClient()

  "Reading an existing schema" should "return the schema" in {
    val schemaPath = "/schemas/events/base-event/1.json"
    val expectedSchema: JSONObject = JsonReader.read(schemaPath)

    val schemaStream = resourceSchemaClient(schemaPath)
    val schema: JSONObject = JsonReader.read(Source.fromInputStream(schemaStream))

    assert(schema.toString === expectedSchema.toString)
  }

  "Reading a non existing schema" should "throw an exception" in {
    val schemaPath = "/schemas/non-existing.json"

    intercept[SchemaNotFoundException] {
      resourceSchemaClient(schemaPath)
    }.getMessage should equal(s"Schema [$schemaPath] not found")
  }

}
