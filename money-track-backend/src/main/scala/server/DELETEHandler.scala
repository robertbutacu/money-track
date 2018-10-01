package server

import akka.http.scaladsl.model.{HttpResponse, StatusCode}
import data.{Common, Transaction}

object DELETEHandler {
  def deleteTransaction(transaction: Transaction): HttpResponse = {
    val transactionRecord = Common.buildMongoDbObject(transaction)

    MongoFactory.collection.remove(transactionRecord)

    if(MongoFactory.collection.find(transactionRecord).nonEmpty)
      HttpResponse(StatusCode.int2StatusCode(500))
    else
      HttpResponse(StatusCode.int2StatusCode(200))
  }
}
