package com.afranzi.spark.jsonschemas

import com.afranzi.spark.jsonschemas.Exceptions.SchemaNotFoundException
import org.everit.json.schema.Schema
import org.everit.json.schema.loader.{SchemaClient, SchemaLoader}
import org.json.JSONObject

import scala.util.{Failure, Success, Try}

case class SchemaBuilder(schemaClient: Option[SchemaClient] = None) {

  def buildSchema(schema: JSONObject): Schema = {
    val schemaBuilder = SchemaLoader.builder()
      .schemaJson(schema)

    schemaClient
      .fold(schemaBuilder)(schemaBuilder.schemaClient)
      .draftV7Support()
      .build()
      .load()
      .build()
  }

  def loadSchema(schemaPath: String): Schema = {
    val schemaObject = Try(JsonReader.read(schemaPath)) match {
      case Success(reader) => reader
      case Failure(_: NullPointerException) => throw SchemaNotFoundException(schemaPath)
      case Failure(exception) => throw exception
    }
    buildSchema(schemaObject)
  }

}
