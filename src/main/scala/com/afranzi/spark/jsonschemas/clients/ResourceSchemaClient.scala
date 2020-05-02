package com.afranzi.spark.jsonschemas.clients

import java.io.InputStream

import com.afranzi.spark.jsonschemas.Exceptions.SchemaNotFoundException
import org.everit.json.schema.loader.SchemaClient

import scala.util.{Failure, Success, Try}

class ResourceSchemaClient extends SchemaClient {

  def loadResource(url: String): InputStream = {
    getClass.getResourceAsStream(url)
  }

  override def get(url: String): InputStream = {
    Try {
      loadResource(url)
    } match {
      case Success(value: InputStream) => value
      case Success(_) => throw SchemaNotFoundException(url)
      case Failure(_) => throw SchemaNotFoundException(url)
    }
  }

}

