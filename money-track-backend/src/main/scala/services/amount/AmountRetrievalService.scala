package services.amount

import akka.actor.{Actor, ActorSystem, Props}
import grizzled.slf4j.Logging
import services.transaction.TransactionsRetrievalService

class AmountRetrievalService(system: ActorSystem) extends Actor with Logging {
  val transactionRetrievalActor = system.actorOf(Props[TransactionsRetrievalService], name = "transactionsRetrievalService")

/*


  def getAmountForLastNDaysWithoutBills(n: Int): Amount = {
    logger.info(s"[ ${getCurrentDate()} ] *** Retrieving amount for transactions excepting bills for last $n days.")

    val transactions = getTransactionsForLastNDays(n)

    gatherAmount(transactions.filterNot(_.isBill))
  }

  def getBudgetRemaining(start: Date, end: Date, limit: Double): Amount = {
    logger.info(s"[${getCurrentDate()} *** Trying to get remaining budget")

    val transactions = getTransactionsByPeriod(start, end).filterNot(_.isBill)
    logger.info(s"[${getCurrentDate()} ] *** Found $transactions.")

    val amountSpent = gatherAmount(transactions).amount
    logger.info(s"[ ${getCurrentDate()} ] *** Amount spent ups to $amountSpent.")

    Amount(limit - amountSpent)
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

  private def gatherAmount(transactions: Transactions): Amount =
    Amount(transactions.foldLeft(0.0)((acc, curr) => acc + curr.amount))
*/

  override def receive: Receive = {
    case _ => logger.info("Received message in transactions retrieval")
  }
}
