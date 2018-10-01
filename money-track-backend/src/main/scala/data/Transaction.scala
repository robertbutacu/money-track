package data

import java.text.SimpleDateFormat
import java.util.Date

import com.mongodb.casbah.Imports._

case class Transaction(name: String, category: Option[String] = None, amount: BigDecimal, date: Date)

object Common {
  /**
    * Convert a Stock object into a BSON format that MongoDb can store.
    */
  def buildMongoDbObject(transaction: Transaction): MongoDBObject = {
    val builder = MongoDBObject.newBuilder
    builder += "name" -> transaction.name
    builder += "category" -> transaction.category.getOrElse("")
    builder += "amount" -> transaction.amount
    builder += "date" -> new SimpleDateFormat("dd-mm-YYYY").format(transaction.date)

    builder.result
  }
}