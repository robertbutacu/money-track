package server

import java.text.SimpleDateFormat
import java.util.Date

import data.{Common, Transaction}

object GETHandler {
  def getByDay(day: Date): List[Transaction] = {
    val toFindRecord = new SimpleDateFormat("dd-mm-YYYY").format(day)
    MongoFactory.collection.find(toFindRecord).toList.map(Common.fromMongoDbObject)
  }

  def getByPeriod(start: Date, end: Date): List[Transaction] = ???

  def getAmountSpentByDate(day: Date): BigDecimal = {
    val transactions = getByDay(day)

    transactions.foldLeft(0: BigDecimal){
      case(acc, curr) =>
        acc + curr.amount
    }
  }

  def getAmountSpentByPeriod(start: Date, end: Date): BigDecimal = {
    val transactions = getByPeriod(start, end)

    transactions.foldLeft(0: BigDecimal){
      case(acc, curr) =>
        acc + curr.amount
    }
  }
}
