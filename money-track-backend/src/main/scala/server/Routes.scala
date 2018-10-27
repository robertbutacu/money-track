package server

import java.text.SimpleDateFormat
import java.util.Date

import akka.actor._
import akka.pattern._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import controllers.AmountController.AmountRoutes
import controllers.TransactionController.TransactionRoutes
import controllers.{AmountController, TransactionController}
import grizzled.slf4j.Logging
import marshaller.Marshaller

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

class Routes(actorSystem: ActorSystem)(implicit timeout: Timeout, executionContext: ExecutionContext) extends Marshaller with Logging {
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

  val transactionController = actorSystem.actorOf(Props(new TransactionController(actorSystem)), name = "transactionController")
  val amountController = actorSystem.actorOf(Props(new AmountController(actorSystem)), name = "amountController")

  def getCurrentDate(): String = loggingDateFormatter.format(new Date())

  val routes: Route = {
    val allRoutes = for {
      amountRoutes <- (amountController ? AmountRoutes).mapTo[Route]
      transactionsRoutes <- (transactionController ? TransactionRoutes).mapTo[Route]
    } yield amountRoutes ~ transactionsRoutes

    Await.result(allRoutes, Duration(5, "seconds"))
  }
}
