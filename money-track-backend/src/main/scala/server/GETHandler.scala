package server

import java.text.SimpleDateFormat
import java.util.Date
import com.mongodb.casbah.Imports._


import data.{Common, Transaction}

object GETHandler {
  def getByDay(day: Date): List[Transaction] = {
    val toFindRecord = new SimpleDateFormat("dd-mm-YYYY").format(day)
    val results = MongoFactory.collection.find(toFindRecord).toIterable

    convertToList(results)
  }

  def getByPeriod(start: Date, end: Date): List[Transaction] = {
    val results = MongoFactory.collection.filter { record =>
      val transactionDate = record.getAs[Date]("date")

      transactionDate.forall(date => date.after(start) && date.before(end))
    }

    convertToList(results)
  }

  def getAmountSpentByDate(day: Date): BigDecimal = {
    val transactions = getByDay(day)

    transactions.foldLeft(0: BigDecimal) {
      case (acc, curr) =>
        acc + curr.amount
    }
  }

  def getAmountSpentByPeriod(start: Date, end: Date): BigDecimal = {
    val transactions = getByPeriod(start, end)

    transactions.foldLeft(0: BigDecimal) {
      case (acc, curr) =>
        acc + curr.amount
    }
  }

  private def convertToList(l: Iterable[DBObject]): List[Transaction] =
    l.toList.map(Common.fromMongoDbObject)
}
