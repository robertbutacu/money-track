package server

import java.text.SimpleDateFormat
import java.util.Date

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.{AmountController, TransactionController}
import grizzled.slf4j.Logging
import marshaller.Marshaller

object Routes extends Marshaller with Logging {
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

  def getCurrentDate(): String = loggingDateFormatter.format(new Date())

  val route: Route = AmountController.amountRoutes ~ TransactionController.transactionRoutes
}
