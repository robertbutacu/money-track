package server

import java.text.SimpleDateFormat
import java.time.LocalDate

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import data.Transaction
import server.marshaller.Marshaller
import server.services.GETHandler


object Routes extends Marshaller {
  lazy val dateFormatter = new SimpleDateFormat("dd-MM-yyyy")

  val route: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    } ~ path("transaction") {
      post {
        entity(as[Transaction]) { t =>
          println(t)
          complete(StatusCodes.Created)
        }
      }
    } ~ path("amount") {
      get {
        parameter("date".as[String]) { date => {
          val formattedDate = dateFormatter.parse(date)

          println(s"*** Formatted date: $formattedDate for initial input $date")

          val amount = GETHandler.getAmountSpentByDate(formattedDate)

          println(s"*** Found amount: $amount")
          complete(StatusCodes.OK, List(amount))
        }
        }
      }
    } ~ path("amount" / "interval") {
      get {
        parameter("start".as[String], "end".as[String]) { case (start, end) =>
          val startDate = dateFormatter.parse(start)
          val endDate = dateFormatter.parse(end)
          val amount = GETHandler.getAmountSpentByPeriod(startDate, endDate)
          complete(StatusCodes.OK, List(amount))
        }
      }
    } ~ path("amount" / "last") {
      get {
        parameter("days".as[Int]) { n =>
          val endDate = java.sql.Date.valueOf(LocalDate.now)
          val startDate = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

          val amount = GETHandler.getAmountSpentByPeriod(startDate, endDate)
          complete(StatusCodes.OK, List(amount))
        }
      }
    } ~ path("transactions") {
      get {
        parameter("date".as[String]) { date =>
          val formattedDate = dateFormatter.parse(date)

          println(s"*** Formatted date: $formattedDate for initial input $date")

          val transactions = GETHandler.getByDay(formattedDate)

          println(s"*** Found transactions: $transactions")
          complete(StatusCodes.OK, List(transactions))
        }
      }
    } ~ path("transactions" / "interval") {
      get {
        parameter("start".as[String], "end".as[String]) { case (start, end) =>
          val startDate = dateFormatter.parse(start)
          val endDate = dateFormatter.parse(end)
          val transactions = GETHandler.getByPeriod(startDate, endDate)
          complete(StatusCodes.OK, List(transactions))
        }
      }
    } ~ path("transactions" / "last") {
      get {
        parameter("days".as[Int]) { n =>
          val endDate = java.sql.Date.valueOf(LocalDate.now)
          val startDate = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

          val transactions = GETHandler.getByPeriod(startDate, endDate)
          complete(StatusCodes.OK, List(transactions))
        }
      }
    }


}
