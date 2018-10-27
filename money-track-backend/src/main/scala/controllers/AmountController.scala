package controllers

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import controllers.AmountController.AmountRoutes
import marshaller.Marshaller
import models.Common._
import services.GETHandler
import services.amount.AmountRetrievalService

import scala.concurrent.ExecutionContext

class AmountController(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext) extends Marshaller with Actor {
  val amountRetrievalActor = actorSystem.actorOf(Props(new AmountRetrievalService(actorSystem)), "amountRetrievalService")

  val amountRoutes: Route =
    path("amount") {
      get {
        parameter("date".as[String]) { date =>
          val amount = GETHandler.getAmountSpentByDate(date)

          complete(amount)
        }
      } ~ get {
        parameter("product".as[String]) { product =>
          val amount = GETHandler.getAmountByProduct(product)

          complete(amount)
        }
      } ~ get {
        parameter("category".as[String]) { category =>
          val amount = GETHandler.getAmountByCategory(category)

          complete(amount)
        }
      }
    } ~ path("amount" / "interval") {
      get {
        parameter("start".as[String], "end".as[String]) { case (start, end) =>
          val amount = GETHandler.getAmountSpentByPeriod(start, end)

          complete(amount)
        }
      }
    } ~ path("amount" / "budget") {
      get {
        parameter("start".as[String], "end".as[String], "limit".as[Double]) { case (start, end, limit) =>
          val remaining = GETHandler.getBudgetRemaining(start, end, limit)

          complete(remaining)
        }
      }
    } ~ path("amount" / "last" / "withBills") {
      get {
        parameter("days".as[Int]) { n =>
          val amount = GETHandler.getAmountForLastNDays(n)

          complete(amount)
        }
      }
    } ~ path("amount" / "last") {
      get {
        parameter("days".as[Int]) { n =>
          val amount = GETHandler.getAmountForLastNDaysWithoutBills(n)

          complete(amount)
        }
      }
    }

  override def receive: Receive = {
    case AmountRoutes => sender() ! amountRoutes
  }
}

object AmountController {
  case object AmountRoutes
}
