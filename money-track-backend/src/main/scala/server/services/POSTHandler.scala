package server.services

import com.mongodb.WriteResult
import com.mongodb.casbah.Imports._
import data.{Common, Transaction}

object POSTHandler {
  def postTransaction(transaction: Transaction): WriteResult = {
    val transactionRecord = Common.buildMongoDbObject(transaction)
    MongoFactory.collection.save(transactionRecord)
  }
}
