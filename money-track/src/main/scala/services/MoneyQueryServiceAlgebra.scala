package services

import java.util.Date

import cats.Monad
import models.Transactions

trait MoneyQueryServiceAlgebra[F[_]] {
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

class MoneyQueryService[F[_]](implicit M: Monad[F])
    extends MoneyQueryServiceAlgebra[F] {
  override def getForLastNDaysWithoutBills(n: Int): F[Transactions] = ???

  override def getAmountForLastNDaysWithoutBills(n: Int): F[Double] = ???

  override def getByDay(day: Date): F[Transactions] = ???

  override def getBudgetRemaining(start: Date, end: Date, limit: Double): F[Double] = ???

  override def getTransactionsForLastNDays(n: Int): F[Transactions] = ???

  override def getTransactionsByPeriod(start: Date, end: Date): F[Transactions] = ???

  override def getAmountSpentByDate(day: Date): F[Double] = ???

  override def getAmountForLastNDays(n: Int): F[Double] = ???

  override def getByProduct(product: String): F[Transactions] = ???

  override def getByCategory(category: String): F[Transactions] = ???

  override def getAmountByProduct(product: String): F[Double] = ???

  override def getAmountByCategory(category: String): F[Double] = ???

  override def getAmountSpentByPeriod(start: Date, end: Date): F[Double] = ???
}