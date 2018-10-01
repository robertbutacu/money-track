package data

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.mongodb.casbah.Imports._

case class Transaction(name: Option[String], category: Option[String] = None, amount: Double = 0.0, date: Option[Date])

object Common {
  /**
    * Convert a Transaction object into a BSON format that MongoDb can store.
    */
  def buildMongoDbObject(transaction: Transaction): MongoDBObject = {
    val formattedDate = transaction.date.getOrElse(new Date())

    val builder = MongoDBObject.newBuilder
    builder += "name" -> transaction.name
    builder += "category" -> transaction.category
    builder += "amount" -> transaction.amount
    builder += "date" -> new SimpleDateFormat("dd-mm-YYYY").format(formattedDate)

    builder.result
  }

  def buildDateObject(date: String): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "date" -> date

    builder.result
  }

  def fromMongoDbObject(mongoObject: DBObject): Transaction = {
    Transaction(mongoObject.getAs[String]("name"),
      mongoObject.getAs[String]("category"),
      mongoObject.getAs[Double]("amount").getOrElse(0.0),
      mongoObject.getAs[Date]("date"))
  }
}