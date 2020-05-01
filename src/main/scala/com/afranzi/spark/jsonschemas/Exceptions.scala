package com.afranzi.spark.jsonschemas

object Exceptions {

  final case class UndefinedSchemaException() extends Exception

  final case class SchemaNotFoundException(schema: String) extends Exception(s"Schema [$schema] not found")

}
