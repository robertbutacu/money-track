package services.amount

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import cats.data.Writer
import grizzled.slf4j.Logging
import models.{Amount, Transaction}
import services.transaction.TransactionsRetrievalService
import services.transaction.TransactionsRetrievalService._
import cats.syntax.WriterSyntax
import cats.instances._

import scala.concurrent.{ExecutionContext, Future}

class AmountRetrievalService(system: ActorSystem)(implicit timeout: Timeout) extends Actor with Logging {

  //TODO use Writer monad to log into for comprehensions
  val transactionRetrievalActor = system.actorOf(Props[TransactionsRetrievalService], name = "transactionsRetrievalService")
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

  def getCurrentDate(): String = loggingDateFormatter.format(new Date())

  def execute[A](log: A => String, f: => A): Writer[Unit, A] = Writer(logger.info(log(f)), f)

  type Transactions = List[Transaction]

  def getAmountForLastNDaysWithoutBills(n: Int)(implicit ec: ExecutionContext): Future[Amount] = {
    logger.info(s"[ ${getCurrentDate()} ] *** Retrieving amount for transactions excepting bills for last $n days.")

    for {
      transactions <- (transactionRetrievalActor ? GetTransactionsForLastNDays(n)).mapTo[Transactions]
      result = gatherAmount(transactions.filterNot(_.isBill))
    } yield result
  }

  def getBudgetRemaining(start: Date, end: Date, limit: Double)(implicit ec: ExecutionContext): Future[Amount] = {
    logger.info(s"[${getCurrentDate()} *** Trying to get remaining budget")
    for {
      transactions <- (transactionRetrievalActor ? GetTransactionsByPeriod(start, end)).mapTo[Transactions]
      result       <- execute((t: Transactions) => s"[${getCurrentDate()} ] *** Found $t", transactions.filterNot(_.isBill))
      amountSpent  <- execute((s: Double) => s"[ ${getCurrentDate()} ] *** Amount spent ups to $s.", gatherAmount(result).amount)
    } yield Amount(limit - amountSpent)
  }

  def getAmountSpentByDate(day: Date)(implicit ec: ExecutionContext): Future[Amount] = {
    logger.info(s"[ ${getCurrentDate()} ] *** Amount by date: $day")

    for {
      transactions <- (transactionRetrievalActor ? GetByDay(day)).mapTo[Transactions]
      amount       <- execute((a: Amount) => s"[ ${getCurrentDate()} ] *** Found amount: $a", gatherAmount(transactions))
    } yield amount
  }

  def getAmountForLastNDays(n: Int)(implicit ec: ExecutionContext): Future[Amount] = {
    logger.info(s"[ ${getCurrentDate()} ] *** Getting amount for the last $n days")

    val endDate = java.sql.Date.valueOf(LocalDate.now.plusDays(1))
    val startDate = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

    getAmountSpentByPeriod(startDate, endDate)
  }

  def getAmountByProduct(product: String)(implicit ec: ExecutionContext): Future[Amount] =
    for {
      transactions <- (transactionRetrievalActor ? GetByProduct(product)).mapTo[Transactions]
    } yield gatherAmount(transactions)


  def getAmountByCategory(category: String)(implicit ec: ExecutionContext): Future[Amount] =
    for {
      transactions <- (transactionRetrievalActor ? GetByCategory(category)).mapTo[Transactions]
    } yield gatherAmount(transactions)

  def getAmountSpentByPeriod(start: Date, end: Date)(implicit ec: ExecutionContext): Future[Amount] = {
    logger.info(s"[ ${getCurrentDate()} ] *** Getting amount for the interval: $start -> $end")

    for {
      transactions <- (transactionRetrievalActor ? GetTransactionsByPeriod(start, end)).mapTo[Transactions]
      amount <- execute((a: Amount)=> s"[ ${getCurrentDate()} ] *** Extract amount from the database $a", gatherAmount(transactions))
    } yield amount
  }

  private def gatherAmount(transactions: Transactions): Amount =
    Amount(transactions.foldLeft(0.0)((acc, curr) => acc + curr.amount))

  override def receive: Receive = {
    case _ => logger.info("Received message in transactions retrieval")
  }
}
