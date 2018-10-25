package services

import akka.http.scaladsl.model.{HttpResponse, StatusCode}
import com.mongodb.casbah.Imports._
import models.{Common, Transaction}

object DELETEHandler {
  def deleteTransaction(transaction: Transaction): HttpResponse = {
    val transactionRecord = Common.buildMongoDbObject(transaction)

    MongoFactory.collection.remove(transactionRecord)

    if(MongoFactory.collection.find(transactionRecord).nonEmpty)
      HttpResponse(StatusCode.int2StatusCode(500))
    else
      HttpResponse(StatusCode.int2StatusCode(200))
  }

  def deleteAll(): Unit = {
    MongoFactory.collection.drop()
  }
}
