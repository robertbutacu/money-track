package server

import com.mongodb.WriteResult
import data.{Common, Transaction}

object POSTHandler {
  def postTransaction(transaction: Transaction): WriteResult = {
    val transactionRecord = Common.buildMongoDbObject(transaction)
    MongoFactory.collection.save(transactionRecord)
  }
}
