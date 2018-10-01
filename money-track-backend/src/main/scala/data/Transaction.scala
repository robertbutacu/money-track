package data

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.mongodb.casbah.Imports._

case class Transaction(name: String, category: Option[String] = None, amount: Double = 0.0, date: Option[Date])

object Common {
  val dateFormatter = new SimpleDateFormat("dd-MM-YYYY")

  /**
    * Convert a Transaction object into a BSON format that MongoDb can store.
    */
  def buildMongoDbObject(transaction: Transaction): MongoDBObject = {
    val formattedDate = transaction.date.getOrElse(new Date())

    val builder = MongoDBObject.newBuilder
    builder += "name" -> transaction.name
    builder += "category" -> transaction.category
    builder += "amount" -> transaction.amount
    builder += "date" -> dateFormatter.format(formattedDate)

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
      mongoObject.getAs[String]("date") match {
        case Some(date) =>  Some(dateFormatter.parse(date))
        case None => None
      })
  }


}