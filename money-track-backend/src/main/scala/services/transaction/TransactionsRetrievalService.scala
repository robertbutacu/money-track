package services.transaction

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

import akka.actor.Actor
import cats.data.Writer
import com.mongodb.casbah.Imports.{DBObject, _}
import grizzled.slf4j.Logging
import models.{Amount, Common, Transaction}
import services.MongoFactory

import scala.concurrent.{ExecutionContext, Future}

class TransactionsRetrievalService extends Actor with Logging {
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

  def getCurrentDate: String = loggingDateFormatter.format(new Date())

  def execute[A](log: A => String, f: => A): Writer[Unit, A] = Writer(logger.info(log(f)), f)

  type Transactions = List[Transaction]

  def getForLastNDaysWithoutBills(n: Int)(implicit ec: ExecutionContext): Future[Transactions] =
    for {
      transactions <- getTransactionsForLastNDays(n)
      result       <- execute((_: Transactions) => s"[ $getCurrentDate ] *** Retrieving transactions without bills for last $n days.", transactions.filterNot(_.isBill))
    } yield result

  def getByDay(day: Date)(implicit ec: ExecutionContext): Future[Transactions] = Future {
    val toFindRecord = new SimpleDateFormat("dd-MM-yyyy").format(day)
    logger.info(s"[ $getCurrentDate ] *** Finding transactions by date: $toFindRecord")

    val results = MongoFactory.collection.find(Common.buildDateObject(toFindRecord)).toIterable

    val transactions = convertToList(results)
    logger.info(s"[ $getCurrentDate ] *** Found transactions: $transactions")

    transactions
  }


  def getTransactionsForLastNDays(n: Int)(implicit ec: ExecutionContext): Future[Transactions] = {
    val end = java.sql.Date.valueOf(LocalDate.now.plusDays(1))
    val start = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

    logger.info(s"[ $getCurrentDate ] *** Extracting transactions for the last $n days.")

    getTransactionsByPeriod(start, end)
  }

  def getTransactionsByPeriod(start: Date, end: Date)(implicit ec: ExecutionContext): Future[Transactions] = Future {

    logger.info(s"[ $getCurrentDate ] *** Trying to find transactions by interval: $start -> $end")

    val results = MongoFactory.collection.filter { record =>
      val transactionDate = record.getAs[String]("date")

      transactionDate.exists { date =>
        val formattedDate = Common.dateFormatter.parse(date)
        formattedDate.after(start) && formattedDate.before(end)
      }
    }

    val transactions = convertToList(results)

    logger.info(s"[ $getCurrentDate ] *** Found transactions: $transactions")

    convertToList(results)
  }

  def getByProduct(product: String)(implicit ec: ExecutionContext): Future[Transactions] =
    getTransactionsByField("name", product)

  def getByCategory(category: String)(implicit ec: ExecutionContext): Future[Transactions] =
    getTransactionsByField("category", category)

  private def getTransactionsByField(field: String, value: String)(implicit ec: ExecutionContext): Future[Transactions] = Future {
    logger.info(s"[ $getCurrentDate ] *** Getting transactions by field: $field")

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

  case class GetForLastNDaysWithoutBills(n: Int)
  case class GetByDay(day: Date)
  case class GetTransactionsForLastNDays(n: Int)
  case class GetTransactionsByPeriod(start: Date, end: Date)
  case class GetByProduct(product: String)
  case class GetByCategory(category: String)

}