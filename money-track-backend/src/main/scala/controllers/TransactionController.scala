package controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import marshaller.Marshaller
import models.Common._
import models.Transaction
import services.{GETHandler, POSTHandler}

object TransactionController extends Marshaller {
  val transactionRoutes: Route =
    path("transactions" /) {
      get {
        parameter("category".as[String]) { category =>
          val transactions = GETHandler.getByCategory(category)

          complete(StatusCodes.OK, transactions)
        }
      } ~
        post {
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
        } ~ pathSuffix("interval") {
          get {
            parameter("start".as[String], "end".as[String]) { case (start, end) =>
              val transactions = GETHandler.getByPeriod(start, end)

              complete(StatusCodes.OK, transactions)
            }
          }
        } ~ get {
        parameter("product".as[String]) { product =>
          val transactions = GETHandler.getByProduct(product)

          complete(StatusCodes.OK, transactions)

        }
      } ~ path("/last") {
        get {
          parameter("days".as[Int]) { n =>
            val transactions = GETHandler.getForLastNDays(n)

            complete(StatusCodes.OK, transactions)
          }
        }
      }
    }
}
