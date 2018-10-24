package server.services

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

import com.mongodb.casbah.Imports._
import data.{Amount, Common, Transaction}
import grizzled.slf4j.Logging
import server.Routes.getCurrentDate

object GETHandler extends Logging {
  def getByDay(day: Date): List[Transaction] = {
    val toFindRecord = new SimpleDateFormat("dd-MM-yyyy").format(day)
    logger.info(s"[ ${getCurrentDate()} ] *** Finding transactions by date: $toFindRecord")

    val results = MongoFactory.collection.find(Common.buildDateObject(toFindRecord)).toIterable

    val transactions = convertToList(results)
    logger.info(s"[ ${getCurrentDate()} ] *** Found transactions: $transactions")

    transactions
  }

  def getBudgetRemaining(start: Date, end: Date, limit: Double): Amount = {
    logger.info(s"[${getCurrentDate()} *** Trying to get remaining budget")

    val transactions = getByPeriod(start, end).filterNot(_.isBill)
    logger.info(s"[${getCurrentDate()} ] *** Found $transactions.")

    val amountSpent = transactions.foldLeft(0.0)((acc, t) => acc + t.amount)
    logger.info(s"[ ${getCurrentDate()} ] *** Amount spent ups to $amountSpent.")

    Amount(limit - amountSpent)
  }

  def getForLastNDays(n: Int): List[Transaction] = {
    val end = java.sql.Date.valueOf(LocalDate.now)
    val start = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

    logger.info(s"[ ${getCurrentDate()} ] *** Extracting transactions for the last $n days.")

    getByPeriod(start, end)
  }

  def getByPeriod(start: Date, end: Date): List[Transaction] = {

    logger.info(s"[ ${getCurrentDate()} ] *** Trying to find transactions by interval: $start -> $end")

    val results = MongoFactory.collection.filter { record =>
      val transactionDate = record.getAs[String]("date")

      transactionDate.forall{date =>
        val formattedDate = Common.dateFormatter.parse(date)
        formattedDate.after(start) && formattedDate.before(end)}
    }

    val transactions = convertToList(results)

    logger.info(s"[ ${getCurrentDate()} ] *** Found transactions: $transactions")

    convertToList(results)
  }

  def getAmountSpentByDate(day: Date): Amount = {
    logger.info(s"[ ${getCurrentDate()} ] *** Amount by date: $day")

    val transactions = getByDay(day)

    val amount = transactions.foldLeft(0.0) {
      case (acc, curr) =>
        acc + curr.amount
    }

    logger.info(s"[ ${getCurrentDate()} ] *** Found amount: $amount")

    Amount(amount)
  }

  def getAmountForLastNDays(n: Int): Amount = {
    logger.info(s"[ ${getCurrentDate()} ] *** Getting amount for the last $n days")

    val endDate = java.sql.Date.valueOf(LocalDate.now)
    val startDate = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

    getAmountSpentByPeriod(startDate, endDate)
  }

  def getAmountSpentByPeriod(start: Date, end: Date): Amount = {
    logger.info(s"[ ${getCurrentDate()} ] *** Getting amount for the interval: $start -> $end")

    val transactions = getByPeriod(start, end)

    val amount = transactions.foldLeft(0.0) {
      case (acc, curr) =>
        acc + curr.amount
    }

    logger.info(s"[ ${getCurrentDate()} ] *** Extract amount from the database $amount")

    Amount(amount)
  }

  private def convertToList(l: Iterable[DBObject]): List[Transaction] =
    l.toList.map(Common.fromMongoDbObject)
}
