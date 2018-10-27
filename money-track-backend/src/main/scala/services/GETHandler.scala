package services

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

import com.mongodb.casbah.Imports._
import models.{Amount, Common, Transaction}
import grizzled.slf4j.Logging
import marshaller.Marshaller

object GETHandler extends Logging with Marshaller {
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
  def getCurrentDate(): String = loggingDateFormatter.format(new Date())

  def getForLastNDaysWithoutBills(n: Int): Transactions = {
    logger.info(s"[ ${getCurrentDate()} ] *** Retrieving transactions without bills for last $n days.")

    getTransactionsForLastNDays(n).filterNot(_.isBill)
  }

  def getAmountForLastNDaysWithoutBills(n: Int): Amount = {
    logger.info(s"[ ${getCurrentDate()} ] *** Retrieving amount for transactions excepting bills for last $n days.")

    val transactions = getTransactionsForLastNDays(n)

    gatherAmount(transactions.filterNot(_.isBill))
  }

  type Transactions = List[Transaction]

  def getByDay(day: Date): Transactions = {
    val toFindRecord = new SimpleDateFormat("dd-MM-yyyy").format(day)
    logger.info(s"[ ${getCurrentDate()} ] *** Finding transactions by date: $toFindRecord")

    val results = MongoFactory.collection.find(Common.buildDateObject(toFindRecord)).toIterable

    val transactions = convertToList(results)
    logger.info(s"[ ${getCurrentDate()} ] *** Found transactions: $transactions")

    transactions
  }

  def getBudgetRemaining(start: Date, end: Date, limit: Double): Amount = {
    logger.info(s"[${getCurrentDate()} *** Trying to get remaining budget")

    val transactions = getTransactionsByPeriod(start, end).filterNot(_.isBill)
    logger.info(s"[${getCurrentDate()} ] *** Found $transactions.")

    val amountSpent = gatherAmount(transactions).amount
    logger.info(s"[ ${getCurrentDate()} ] *** Amount spent ups to $amountSpent.")

    Amount(limit - amountSpent)
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

  def getAmountSpentByDate(day: Date): Amount = {
    logger.info(s"[ ${getCurrentDate()} ] *** Amount by date: $day")

    val transactions = getByDay(day)

    val amount = gatherAmount(transactions)

    logger.info(s"[ ${getCurrentDate()} ] *** Found amount: ${amount.amount}")

    amount
  }

  def getAmountForLastNDays(n: Int): Amount = {
    logger.info(s"[ ${getCurrentDate()} ] *** Getting amount for the last $n days")

    val endDate = java.sql.Date.valueOf(LocalDate.now.plusDays(1))
    val startDate = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

    getAmountSpentByPeriod(startDate, endDate)
  }

  def getByProduct(product: String): Transactions =
    getTransactionsByField("name", product)

  def getByCategory(category: String): Transactions =
    getTransactionsByField("category", category)

  def getAmountByProduct(product: String): Amount =
    gatherAmount(getByProduct(product))

  def getAmountByCategory(category: String): Amount =
    gatherAmount(getByCategory(category))

  def getAmountSpentByPeriod(start: Date, end: Date): Amount = {
    logger.info(s"[ ${getCurrentDate()} ] *** Getting amount for the interval: $start -> $end")

    val transactions = getTransactionsByPeriod(start, end)

    val amount = gatherAmount(transactions)

    logger.info(s"[ ${getCurrentDate()} ] *** Extract amount from the database ${amount.amount}")

    amount
  }

  private def getTransactionsByField(field: String, value: String): Transactions = {
    logger.info(s"[ ${getCurrentDate()} ] *** Getting transactions by field: $field")

    val transactions = MongoFactory.collection.filter { record =>
      val recordProductName = record.getAs[String](field)

      recordProductName.contains(value)
    }

    convertToList(transactions)
  }

  private def gatherAmount(transactions: Transactions): Amount =
    Amount(transactions.foldLeft(0.0)((acc, curr) => acc + curr.amount))

  private def convertToList(l: Iterable[DBObject]): Transactions =
    l.toList.map(Common.fromMongoDbObject)
}
