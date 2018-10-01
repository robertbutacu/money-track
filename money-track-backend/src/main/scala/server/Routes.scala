package server

import java.text.SimpleDateFormat

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import data.Transaction
import server.marshaller.Marshaller
import server.services.GETHandler


object Routes extends Marshaller {
  /*
  => money-track --get-amount -d=${date} - get the amount spent for a specific day


=> money-track --get-amount -s=${date} --e=${date} - get the amount spent for a specific period


=> money-track --get --d=${date} - retrieves the transactions for a specific date


=> money-track --get --s=${date} --e=${date} - retrieves the transactions for a specific period


=> money-track --l=${n} - retrieves the transactions for the past n days


=> money-track -remove --d=${date} --n=${name} --c=${category} --a=${amount}
                        => since the amount is mandatory, it is crucial to know exactly what is to be deleted
                        => the easiest way to do this is to do a get by a day first and then delete a specific one
   */
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
          val formattedDate = new SimpleDateFormat("dd-MM-yyyy").parse(date)

          println(s"*** Formatted date: $formattedDate for initial input $date")

          val amount = GETHandler.getAmountSpentByDate(formattedDate)

          println(s"*** Found amount: $amount")
          complete(StatusCodes.OK, List(amount))
        }
        }
      }
    }


}
