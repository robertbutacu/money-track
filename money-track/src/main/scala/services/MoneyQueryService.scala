package services

import java.util.Date

import models.Transactions

trait MoneyQueryService[F[_]] {
  def getForLastNDaysWithoutBills(n: Int):                       F[Transactions]
  def getAmountForLastNDaysWithoutBills(n: Int):                 F[Double]
  def getByDay(day: Date):                                       F[Transactions]
  def getBudgetRemaining(start: Date, end: Date, limit: Double): F[Double]
  def getTransactionsForLastNDays(n: Int):                       F[Transactions]
  def getTransactionsByPeriod(start: Date, end: Date):           F[Transactions]
  def getAmountSpentByDate(day: Date):                           F[Double]
  def getAmountForLastNDays(n: Int):                             F[Double]
  def getByProduct(product: String):                             F[Transactions]
  def getByCategory(category: String):                           F[Transactions]
  def getAmountByProduct(product: String):                       F[Double]
  def getAmountByCategory(category: String):                     F[Double]
  def getAmountSpentByPeriod(start: Date, end: Date):            F[Double]
}
