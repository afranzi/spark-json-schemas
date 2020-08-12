package com.afranzi.spark.jsonschemas

import org.scalatest.matchers.should.Matchers
import org.scalatest.{Inspectors, OptionValues}
import org.scalatest.flatspec.AnyFlatSpec

/**
  * FlatSpec - http://www.scalatest.org/user_guide/selecting_a_style
  */
abstract class UnitSpec extends AnyFlatSpec with Matchers with OptionValues with Inspectors
