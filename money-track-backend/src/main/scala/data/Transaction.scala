package data

import java.text.SimpleDateFormat
import java.util.Date

import com.mongodb.casbah.Imports._

case class Transaction(name: Option[String], category: Option[String] = None, amount: BigDecimal = 0: BigDecimal, date: Option[Date])

object Common {
  /**
    * Convert a Stock object into a BSON format that MongoDb can store.
    */
  def buildMongoDbObject(transaction: Transaction): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "name" -> transaction.name
    builder += "category" -> transaction.category
    builder += "amount" -> transaction.amount
    builder += "date" -> new SimpleDateFormat("dd-mm-YYYY").format(transaction.date)

    builder.result
  }

  def fromMongoDbObject(mongoObject: DBObject): Transaction = {
    Transaction(mongoObject.getAs[String]("name"),
      mongoObject.getAs[String]("category"),
      mongoObject.getAs[BigDecimal]("amount").getOrElse(0.0),
      mongoObject.getAs[Date]("date"))
  }
}