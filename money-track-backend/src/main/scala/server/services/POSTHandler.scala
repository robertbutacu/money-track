package server.services

import com.mongodb.WriteResult
import com.mongodb.casbah.Imports._
import data.{Common, Transaction}
import grizzled.slf4j.Logging
import server.Routes.{getCurrentDate, logger}

object POSTHandler extends Logging {
  def postTransaction(transaction: Transaction): WriteResult = {
    logger.info(s"[ ${getCurrentDate()} ] *** Received transation : $transaction . It is to be persisted into the database.")

    val transactionRecord = Common.buildMongoDbObject(transaction)
    val result = MongoFactory.collection.save(transactionRecord)

    logger.info(s"[ ${getCurrentDate()} ] *** Transaction has been persisted.")

    result
  }
}
