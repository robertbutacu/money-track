package server.services

import java.text.SimpleDateFormat
import java.util.Date

import com.mongodb.casbah.Imports._
import data.{Common, Transaction}

object GETHandler {
  def getByDay(day: Date): List[Transaction] = {
    val toFindRecord = new SimpleDateFormat("dd-MM-yyyy").format(day)
    println(s"*** To find record $toFindRecord")
    val results = MongoFactory.collection.find(Common.buildDateObject(toFindRecord)).toIterable

    convertToList(results)
  }

  def getByPeriod(start: Date, end: Date): List[Transaction] = {
    val results = MongoFactory.collection.filter { record =>
      val transactionDate = record.getAs[String]("date")

      transactionDate.forall{date =>
        val formattedDate = Common.dateFormatter.parse(date)
        formattedDate.after(start) && formattedDate.before(end)}
    }

    convertToList(results)
  }

  def getAmountSpentByDate(day: Date): Double = {
    val transactions = getByDay(day)

    transactions.foldLeft(0.0) {
      case (acc, curr) =>
        acc + curr.amount
    }
  }

  def getAmountSpentByPeriod(start: Date, end: Date): Double = {
    val transactions = getByPeriod(start, end)

    transactions.foldLeft(0.0) {
      case (acc, curr) =>
        acc + curr.amount
    }
  }

  private def convertToList(l: Iterable[DBObject]): List[Transaction] =
    l.toList.map(Common.fromMongoDbObject)
}
