package com.afranzi.spark.jsonschemas

import java.io.{BufferedReader, File, Reader}

import com.afranzi.spark.jsonschemas.resources.RightSource.fromSource
import org.json.{JSONObject, JSONTokener}

import scala.io.{BufferedSource, Source}

object JsonReader {

  def readFromContent(content: String): JSONObject = new JSONObject(new JSONTokener(content))

  def read(reader: Reader): JSONObject = {
    val br = new BufferedReader(reader)
    val content: String = Stream.continually(br.readLine()).takeWhile(_ != null).mkString("\n")
    readFromContent(content)
  }

  def read(content: File): JSONObject = {
    read(Source.fromFile(content).reader())
  }

  def read(content: BufferedSource): JSONObject = read(content.reader())

  def read(jsonPath: String): JSONObject = {
    val resource = Source.fromResource(jsonPath)
    read(resource)
  }

}
