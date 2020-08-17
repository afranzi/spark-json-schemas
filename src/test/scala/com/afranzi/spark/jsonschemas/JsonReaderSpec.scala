package com.afranzi.spark.jsonschemas

import java.io.File

import com.afranzi.spark.jsonschemas.resources.RightSource.fromResources
import org.json.JSONObject
import org.scalatest.Assertion

import scala.io.Source

class JsonReaderSpec extends UnitSpec {

  def compareJson(a: JSONObject, b: JSONObject): Assertion = {
    assert(a.toString === b.toString)
  }

  "Reading a JSON from File, Reader & content" should "build the same JSON" in {
    val resourcePath = "/schemas/events/base-event/1.json"

    val jsonReader = Source.fromResources(resourcePath).reader()
    val jsonContent = Source.fromResources(resourcePath).getLines().mkString("\n")

    val jsonFile = new File(getClass.getResource(resourcePath).getPath)

    val jsonFromPath = JsonReader.read(resourcePath)
    val jsonFromReader = JsonReader.read(jsonReader)
    val jsonFromContent = JsonReader.readFromContent(jsonContent)
    val jsonFromFile = JsonReader.read(jsonFile)

    compareJson(jsonFromContent, jsonFromReader)
    compareJson(jsonFromContent, jsonFromFile)
    compareJson(jsonFromFile, jsonFromReader)
    compareJson(jsonFromPath, jsonFromContent)
  }

}
