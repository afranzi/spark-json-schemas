package com.afranzi.spark.jsonschemas

import org.apache.spark.SparkException
import org.apache.spark.sql.types._

import scala.collection.mutable.ArrayBuffer
import scala.util.control.NonFatal

object SchemaMerger {

  // Code extracted from org.apache.spark.sql.types.StructType
  // Since it's private ¯\﹍(ツ)﹍/¯
  def merge(left: DataType, right: DataType): DataType =
    (left, right) match {
      case (ArrayType(leftElementType, leftContainsNull),
        ArrayType(rightElementType, rightContainsNull)) =>
        ArrayType(
          merge(leftElementType, rightElementType),
          leftContainsNull || rightContainsNull
        )

      case (MapType(leftKeyType, leftValueType, leftContainsNull),
        MapType(rightKeyType, rightValueType, rightContainsNull)) =>
        MapType(
          merge(leftKeyType, rightKeyType),
          merge(leftValueType, rightValueType),
          leftContainsNull || rightContainsNull
        )

      case (StructType(leftFields), StructType(rightFields)) =>
        val newFields = ArrayBuffer.empty[StructField]

        val rightMapped = fieldsMap(rightFields)
        leftFields.foreach {
          case leftField @ StructField(leftName, leftType, leftNullable, _) =>
            rightMapped.get(leftName)
              .map {
                case rightField @ StructField(rightName, rightType, rightNullable, _) =>
                  try {
                    leftField.copy(
                      dataType = merge(leftType, rightType),
                      nullable = leftNullable || rightNullable
                    )
                  } catch {
                    case NonFatal(e) =>
                      throw new SparkException(s"Failed to merge fields '$leftName' and " +
                        s"'$rightName'. " + e.getMessage)
                  }
              }
              .orElse {
                Some(leftField)
              }
              .foreach(newFields += _)
        }

        val leftMapped = fieldsMap(leftFields)
        rightFields
          .filterNot(f => leftMapped.get(f.name).nonEmpty)
          .foreach { f =>
            newFields += f
          }

        StructType(newFields)

      case (Fixed(leftPrecision, leftScale), Fixed(rightPrecision, rightScale)) =>
        if ((leftPrecision == rightPrecision) && (leftScale == rightScale)) {
          DecimalType(leftPrecision, leftScale)
        } else if ((leftPrecision != rightPrecision) && (leftScale != rightScale)) {
          throw new SparkException("Failed to merge decimal types with incompatible " +
            s"precision $leftPrecision and $rightPrecision & scale $leftScale and $rightScale")
        } else if (leftPrecision != rightPrecision) {
          throw new SparkException("Failed to merge decimal types with incompatible " +
            s"precision $leftPrecision and $rightPrecision")
        } else {
          throw new SparkException("Failed to merge decimal types with incompatible " +
            s"scala $leftScale and $rightScale")
        }

      case (leftType, rightType) if leftType == rightType =>
        leftType

      case _ =>
        throw new SparkException(s"Failed to merge incompatible data types ${left.catalogString}" +
          s" and ${right.catalogString}")
    }

  object Fixed {
    def unapply(t: DecimalType): Option[(Int, Int)] = Some((t.precision, t.scale))
  }

  def fieldsMap(fields: Array[StructField]): Map[String, StructField] = {
    import scala.collection.breakOut
    fields.map(s => (s.name, s))(breakOut)
  }

}
