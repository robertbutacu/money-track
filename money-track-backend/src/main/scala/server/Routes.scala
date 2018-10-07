package server

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import data.Transaction
import server.marshaller.Marshaller
import server.services.{GETHandler, POSTHandler}
import grizzled.slf4j.Logging

object Routes extends Marshaller with Logging{
  lazy val dateFormatter = new SimpleDateFormat("dd-MM-yyyy")
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")

  def getCurrentDate(): String = loggingDateFormatter.format(new Date())


  val route: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    } ~ path("transaction") {
      post {
        entity(as[Transaction]) { t =>
          logger.info(s"[ ${getCurrentDate()} ] *** Received transation : $t . It is to be persisted into the database.")

          POSTHandler.postTransaction(t)

          logger.info(s"[ ${getCurrentDate()} ] *** Transaction has been persisted.")
          complete(StatusCodes.Created)
        }
      }
    } ~ path("amount") {
      get {
        parameter("date".as[String]) { date => {
          val formattedDate = dateFormatter.parse(date)

          logger.info(s"[ ${getCurrentDate()} ] *** Formatted date: $formattedDate for initial input $date")

          val amount = GETHandler.getAmountSpentByDate(formattedDate)

          logger.info(s"[ ${getCurrentDate()} ] *** Found amount: $amount")

          complete(StatusCodes.OK, List(amount))
        }
        }
      }
    } ~ path("amount" / "interval") {
      get {
        parameter("start".as[String], "end".as[String]) { case (start, end) =>
          val startDate = dateFormatter.parse(start)
          val endDate = dateFormatter.parse(end)

          logger.info(s"[ ${getCurrentDate()} ] *** Getting amount for the interval: $startDate -> $endDate")

          val amount = GETHandler.getAmountSpentByPeriod(startDate, endDate)

          logger.info(s"[ ${getCurrentDate()} ] *** Extract amount from the database $amount")

          complete(StatusCodes.OK, List(amount))
        }
      }
    } ~ path("amount" / "last") {
      get {
        parameter("days".as[Int]) { n =>

          logger.info(s"[ ${getCurrentDate()} ] *** Getting amount for the last $n days")

          val endDate = java.sql.Date.valueOf(LocalDate.now)
          val startDate = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

          val amount = GETHandler.getAmountSpentByPeriod(startDate, endDate)

          logger.info(s"[ ${getCurrentDate()} ] *** Extracted amount from the database: $amount")

          complete(StatusCodes.OK, List(amount))
        }
      }
    } ~ path("transactions") {
      get {
        parameter("date".as[String]) { date =>
          val formattedDate = dateFormatter.parse(date)

          logger.info(s"[ ${getCurrentDate()} ] *** Formatted date: $formattedDate for initial input $date")

          val transactions = GETHandler.getByDay(formattedDate)

          logger.info(s"[ ${getCurrentDate()} ] *** Found transactions: $transactions")

          complete(StatusCodes.OK, List(transactions))
        }
      }
    } ~ path("transactions" / "interval") {
      get {
        parameter("start".as[String], "end".as[String]) { case (start, end) =>
          val startDate = dateFormatter.parse(start)
          val endDate = dateFormatter.parse(end)

          logger.info(s"[ ${getCurrentDate()} ] *** Trying to find transactions by interval: $startDate -> $endDate")

          val transactions = GETHandler.getByPeriod(startDate, endDate)

          logger.info(s"[ ${getCurrentDate()} ] *** Found transactions: $transactions")
          complete(StatusCodes.OK, List(transactions))
        }
      }
    } ~ path("transactions" / "last") {
      get {
        parameter("days".as[Int]) { n =>
          val endDate = java.sql.Date.valueOf(LocalDate.now)
          val startDate = java.sql.Date.valueOf(LocalDate.now.minusDays(n))

          logger.info(s"[ ${getCurrentDate()} ] *** Extracting transactions for the last $n days.")
          val transactions = GETHandler.getByPeriod(startDate, endDate)

          logger.info(s"[ ${getCurrentDate()} ] *** Found transactions: $transactions")

          complete(StatusCodes.OK, List(transactions))
        }
      }
    }


}
