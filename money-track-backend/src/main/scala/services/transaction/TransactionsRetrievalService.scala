package services.transaction

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

import akka.actor.Actor
import com.mongodb.casbah.Imports.{DBObject, _}
import grizzled.slf4j.Logging
import models.{Amount, Common, Transaction}
import services.MongoFactory

class TransactionsRetrievalService extends Actor with Logging {
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
  def getCurrentDate(): String = loggingDateFormatter.format(new Date())

  type Transactions = List[Transaction]

  def getForLastNDaysWithoutBills(n: Int): Transactions = {
    logger.info(s"[ ${getCurrentDate()} ] *** Retrieving transactions without bills for last $n days.")

    getTransactionsForLastNDays(n).filterNot(_.isBill)
  }

  def getByDay(day: Date): Transactions = {
    val toFindRecord = new SimpleDateFormat("dd-MM-yyyy").format(day)
    logger.info(s"[ ${getCurrentDate()} ] *** Finding transactions by date: $toFindRecord")

    val results = MongoFactory.collection.find(Common.buildDateObject(toFindRecord)).toIterable

    val transactions = convertToList(results)
    logger.info(s"[ ${getCurrentDate()} ] *** Found transactions: $transactions")

    transactions
  }


  def getTransactionsForLastNDays(n: Int): Transactions = {
    val end = java.sql.Date.valueOf(LocalDate.now.plusDays(1))
    val start = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

    logger.info(s"[ ${getCurrentDate()} ] *** Extracting transactions for the last $n days.")

    getTransactionsByPeriod(start, end)
  }

  def getTransactionsByPeriod(start: Date, end: Date): Transactions = {

    logger.info(s"[ ${getCurrentDate()} ] *** Trying to find transactions by interval: $start -> $end")

    val results = MongoFactory.collection.filter { record =>
      val transactionDate = record.getAs[String]("date")

      transactionDate.exists { date =>
        val formattedDate = Common.dateFormatter.parse(date)
        formattedDate.after(start) && formattedDate.before(end)}
    }

    val transactions = convertToList(results)

    logger.info(s"[ ${getCurrentDate()} ] *** Found transactions: $transactions")

    convertToList(results)
  }

  def getByProduct(product: String): Transactions =
    getTransactionsByField("name", product)

  def getByCategory(category: String): Transactions =
    getTransactionsByField("category", category)

  private def gatherAmount(transactions: Transactions): Amount =
    Amount(transactions.foldLeft(0.0)((acc, curr) => acc + curr.amount))

  private def getTransactionsByField(field: String, value: String): Transactions = {
    logger.info(s"[ ${getCurrentDate()} ] *** Getting transactions by field: $field")

    val transactions = MongoFactory.collection.filter { record =>
      val recordProductName = record.getAs[String](field)

      recordProductName.contains(value)
    }

    convertToList(transactions)
  }

  private def convertToList(l: Iterable[DBObject]): Transactions =
    l.toList.map(Common.fromMongoDbObject)

  override def receive: Receive = {
    case _ => logger.info("Received message in transactions retrieval")
  }
}

object TransactionsRetrievalService {
}