package services

import cats.Monad
import models.Transaction

trait ManagementServiceAlgebra[F[_]] {
  def deleteTransaction(transaction: Transaction): F[Unit]
  def deleteAll():       F[Unit]
  def postWeeklyExpenses():  F[Unit]
  def postMonthlyExpenses(): F[Unit]
  def postTransaction(transaction: Transaction): F[Unit]
}

class ManagementService[F[_]](implicit M: Monad[F])
        extends ManagementServiceAlgebra[F] {
  override def deleteTransaction(transaction: Transaction): F[Unit] = ???

  override def deleteAll(): F[Unit] = ???

  override def postWeeklyExpenses(): F[Unit] = ???

  override def postMonthlyExpenses(): F[Unit] = ???

  override def postTransaction(transaction: Transaction): F[Unit] = ???
}
