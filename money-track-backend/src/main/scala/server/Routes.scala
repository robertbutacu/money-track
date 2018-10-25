package server

import java.text.SimpleDateFormat
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.Transaction
import grizzled.slf4j.Logging
import marshaller.Marshaller
import services.{GETHandler, POSTHandler}

object Routes extends Marshaller with Logging {
  lazy val dateFormatter = new SimpleDateFormat("dd-MM-yyyy")
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

  implicit def stringToDateFormatter(date: String) = dateFormatter.parse(date)

  def getCurrentDate(): String = loggingDateFormatter.format(new Date())


  val route: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    } ~ path("transaction") {
      post {
        entity(as[Transaction]) { t =>
          POSTHandler.postTransaction(t)
          complete(StatusCodes.Created)
        }
      }
    } ~ path("amount") {
      get {
        parameter("date".as[String]) { date => {
          val amount = GETHandler.getAmountSpentByDate(date)

          complete(amount)
        }
        }
      }
    } ~ path("amount" / "interval") {
      get {
        parameter("start".as[String], "end".as[String]) { case (start, end) =>
          val amount = GETHandler.getAmountSpentByPeriod(start, end)

          complete(amount)
        }
      }
    } ~ path("amount" / "last") {
      get {
        parameter("days".as[Int]) { n =>
          val amount = GETHandler.getAmountForLastNDays(n)

          complete(amount)
        }
      }
    } ~ path("transactions") {
      get {
        parameter("date".as[String]) { date =>
          val transactions = GETHandler.getByDay(date)

          complete(StatusCodes.OK, transactions)
        }
      }
    } ~ path("transactions" / "interval") {
      get {
        parameter("start".as[String], "end".as[String]) { case (start, end) =>
          val transactions = GETHandler.getByPeriod(start, end)

          complete(StatusCodes.OK, transactions)
        }
      }
    } ~ path("transactions" / "last") {
      get {
        parameter("days".as[Int]) { n =>
          val transactions = GETHandler.getForLastNDays(n)

          complete(StatusCodes.OK, transactions)
        }
      }
    } ~ path("budget") {
      get {
        parameter("start".as[String], "end".as[String], "limit".as[Double]) { case (start, end, limit) =>
        val remaining = GETHandler.getBudgetRemaining(start, end, limit)

          complete(remaining)
        }
      }
    } ~ path("transactions") {
      get {
        parameter("product".as[String]) { product =>
          val transactions = GETHandler.getByProduct(product)

          complete(StatusCodes.OK, transactions)

        }
      }
    }


}
