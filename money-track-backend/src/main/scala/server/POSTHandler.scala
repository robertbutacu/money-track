package server

import akka.http.scaladsl.model.HttpResponse
import data.Transaction

object POSTHandler {
  def postTransaction(transaction: Transaction): HttpResponse = ???
}
