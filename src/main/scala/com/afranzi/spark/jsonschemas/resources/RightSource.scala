package com.afranzi.spark.jsonschemas.resources

import java.io.InputStream

import scala.io.{BufferedSource, Codec, Source}

object RightSource {
  def fromResources(path: String)(implicit codec: Codec): BufferedSource = {
    val fileStream: InputStream = getClass.getResourceAsStream(path)
    Source.fromInputStream(fileStream)
  }

  implicit def fromResources(source: Source.type): RightSource.type = RightSource
}

