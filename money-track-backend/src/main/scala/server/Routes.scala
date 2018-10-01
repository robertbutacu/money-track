package server

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import data.Transaction
import server.marshaller.Marshaller
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._


object Routes extends Marshaller {
  val route: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    } ~ path("test") {
      post {
        entity(as[Transaction]) { t =>
          println(t)
          complete(StatusCodes.Created)
        }
      }
    }

}
