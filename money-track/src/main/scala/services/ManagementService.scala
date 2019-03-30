package services

import models.Transaction

trait ManagementService[F[_]] {
  def deleteTransaction(transaction: Transaction): F[Unit]
  def deleteAll():       F[Unit]
  def weeklyExpenses():  F[Unit]
  def monthlyExpenses(): F[Unit]
  def postTransaction(transaction: Transaction): F[Unit]
}
