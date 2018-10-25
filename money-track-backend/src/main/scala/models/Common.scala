package models

import java.text.SimpleDateFormat
import java.util.Date

import com.mongodb.casbah.Imports._

import scala.language.implicitConversions

object Common {
  val dateFormatter = new SimpleDateFormat("dd-MM-yyyy")

  implicit def stringToDateFormatter(date: String): Date = dateFormatter.parse(date)

  /**
    * Convert a Transaction object into a BSON format that MongoDb can store.
    */
  def buildMongoDbObject(transaction: Transaction): MongoDBObject = {
    val formattedDate = transaction.date.getOrElse(new Date())

    val builder = MongoDBObject.newBuilder
    builder += "name"     -> transaction.name
    builder += "category" -> transaction.category
    builder += "amount"   -> transaction.amount
    builder += "date"     -> dateFormatter.format(formattedDate)
    builder += "isBill"   -> transaction.isBill
    builder.result
  }

  def buildDateObject(date: String): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "date" -> date

    builder.result
  }

  def fromMongoDbObject(mongoObject: DBObject): Transaction = {
    Transaction(mongoObject.getAsOrElse[String]("name", ""),
      mongoObject.getAs[String]("category"),
      mongoObject.getAsOrElse[Double]("amount", 0.0),
      mongoObject.getAs[String]("date").map(date => dateFormatter.parse(date)),
      mongoObject.getAsOrElse[Boolean]("isBill", false))
  }


}