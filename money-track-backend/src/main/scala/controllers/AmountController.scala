package controllers

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import marshaller.Marshaller
import models.Common._
import services.GETHandler
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object AmountController extends Marshaller {
  val amountRoutes: Route =
    path("amount") {
      get {
        parameter("date".as[String]) { date =>
          val amount = GETHandler.getAmountSpentByDate(date)

          complete(amount)
        }
      } ~ path("interval") {
        get {
          parameter("start".as[String], "end".as[String]) { case (start, end) =>
            val amount = GETHandler.getAmountSpentByPeriod(start, end)

            complete(amount)
          }
        }
      } ~ path("last") {
        get {
          parameter("days".as[Int]) { n =>
            val amount = GETHandler.getAmountForLastNDays(n)

            complete(amount)
          }
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
    } ~ path("budget") {
      get {
        parameter("start".as[String], "end".as[String], "limit".as[Double]) { case (start, end, limit) =>
          val remaining = GETHandler.getBudgetRemaining(start, end, limit)

          complete(remaining)
        }
      }
    }
}
