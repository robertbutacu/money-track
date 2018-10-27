package controllers

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.TransactionController.TransactionRoutes
import marshaller.Marshaller
import models.Common._
import models.Transaction
import services.transaction.{TransactionsPersistanceService, TransactionsRetrievalService}
import services.{GETHandler, POSTHandler}

import scala.concurrent.ExecutionContext

class TransactionController(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) extends Marshaller with Actor {
  val transactionsPersistenceActor = actorSystem.actorOf(Props[TransactionsPersistanceService], "transactionsPersistenceService")
  val transactionsRetrievalActor = actorSystem.actorOf(Props[TransactionsRetrievalService], "transactionsRetrievalService")

  val transactionRoutes: Route =
    path("transactions") {
      get {
        parameter("category".as[String]) { category =>
          val transactions = GETHandler.getByCategory(category)

          complete(StatusCodes.OK, transactions)
        }
      } ~ post {
        entity(as[Transaction]) { t =>
          POSTHandler.postTransaction(t)
          complete(StatusCodes.Created)
        }
      } ~
        get {
          parameter("date".as[String]) { date =>
            val transactions = GETHandler.getByDay(date)

            complete(StatusCodes.OK, transactions)
          }
        } ~ get {
        parameter("product".as[String]) { product =>
          val transactions = GETHandler.getByProduct(product)

          complete(StatusCodes.OK, transactions)

        }
      }
    } ~ path("transactions" / "interval") {
      get {
        parameter("start".as[String], "end".as[String]) { case (start, end) =>
          val transactions = GETHandler.getTransactionsByPeriod(start, end)

          complete(StatusCodes.OK, transactions)
        }
      }
    } ~ path("transactions" / "last") {
      get {
        parameter("days".as[Int]) { n =>
          val transactions = GETHandler.getForLastNDaysWithoutBills(n)

          complete(StatusCodes.OK, transactions)
        }
      }
    } ~ path("transactions" / "last" / "withBills") {
      get {
        parameter("days".as[Int]) { n =>
          val transactions = GETHandler.getTransactionsForLastNDays(n)

          complete(StatusCodes.OK, transactions)
        }
      }
    } ~ path("transactions" / "monthly") {
      post {
        entity(as[Unit]) { _ =>
          POSTHandler.monthlyExpenses()

          complete(StatusCodes.OK)
        }
      }
    } ~ path("transactions" / "weekly") {
      post {
        entity(as[Unit]) { _ =>
          POSTHandler.weeklyExpenses()

          complete(StatusCodes.OK)
        }
      }
    }

  override def receive: Receive = {
    case TransactionRoutes => sender() ! transactionRoutes
  }
}

object TransactionController {
  case object TransactionRoutes
}
