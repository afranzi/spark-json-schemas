package com.afranzi.spark.jsonschemas

import grizzled.slf4j.Logging
import org.apache.spark.sql.types._
import org.everit.json.schema._

import scala.collection.JavaConversions._

object SparkSchema extends Logging {

  implicit class ObjectSchemaFields(s: ObjectSchema) {
    def fields: Seq[StructField] = {
      s.getPropertySchemas
        .toSeq
        .flatMap {
          case (key, value) =>
            extractDataType(key, value)
        }
        .mergeFields
        .sortBy(_.name)
    }
  }

  implicit class CombinedSchemaFields(s: CombinedSchema) {
    def fields(key: String): Seq[StructField] = {
      s.getSubschemas
        .toSeq
        .flatMap(ss => extractDataType(key, ss))
        .filter(f => f.dataType match {
          case StructType(fields) if fields.length == 0 => false
          case _ => true
        })
        .mergeFields
        .sortBy(_.name)
    }
  }

  implicit class StructFieldsMergeable(fields: Seq[StructField]) {
    def mergeFields: Seq[StructField] = {
      fields
        .groupBy(_.name)
        .mapValues {
          case head +: Nil => head
          case head +: tail => tail.foldLeft(head) { (a, l) =>
            StructField(l.name, SchemaMerger.merge(a.dataType, l.dataType))
          }
        }
        .values
        .toSeq
        .sortBy(_.name)
    }
  }

  private[jsonschemas] def extractDataType[T >: DataType](key: String, schema: Schema): Option[StructField] = {
    logger.debug(s"Primitive ${schema.getClass}")
    val dataType: Option[DataType] = schema match {
      case _: ConstSchema => None
      case _: EnumSchema  => None
      case _: NullSchema  => None
      case s: NumberSchema =>
        if (s.requiresInteger) Some(IntegerType)
        else Some(FloatType)
      case s: StringSchema    => Some(StringType)
      case s: BooleanSchema   => Some(BooleanType)
      case s: ReferenceSchema => extractDataType(key, s.getReferredSchema).map(_.dataType)
      case s: CombinedSchema =>
        s.fields(key) match {
          case Nil         => None
          case head +: Nil => Some(head.dataType)
          case list        => Some(StructType(list))
        }
      case s: ObjectSchema => Some(StructType(s.fields))
      case _ =>
        logger.error(s"None detected $schema")
        None
    }
    dataType.map(dt => StructField(name = key, dataType = dt, nullable = true))
  }

  private[jsonschemas] def inspectSchema(schema: Schema): Seq[StructField] = {
    schema match {
      case s: CombinedSchema  => s.getSubschemas.toSeq.flatMap(inspectSchema).mergeFields
      case s: ObjectSchema    => s.fields
      case s: ReferenceSchema => inspectSchema(s.getReferredSchema)
      case s: Schema =>
        logger.error(s"Schema ${s.getClass} - $s")
        Seq.empty
    }
  }

  implicit class SparkSchemaExtractor(schema: Schema) extends Logging {

    def sparkSchema: StructType = {
      val fields: Seq[StructField] = inspectSchema(schema).sortBy(_.name)
      StructType(fields)
    }

  }

}

