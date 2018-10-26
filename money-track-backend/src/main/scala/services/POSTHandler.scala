package services

import java.util.Date

import com.mongodb.WriteResult
import com.mongodb.casbah.Imports._
import models.{Common, Transaction}
import grizzled.slf4j.Logging
import server.Routes.{getCurrentDate, logger}

object POSTHandler extends Logging {
  def weeklyExpenses(): Unit = {
    logger.info(s"[ ${getCurrentDate()} ] *** Persisting weekly expenses")

    val train = Transaction("train", Some("transport"), 40, Some(new Date()), isBill = true)

    MongoFactory.collection.save(train)
  }

  def monthlyExpenses(): Unit = {
    logger.info(s"[ ${getCurrentDate()} ] *** Persisting monthly expenses")

    val gym = Transaction("gym", Some("sports"), 25, Some(new Date()), isBill = true)
    val swimmingPool = Transaction("pool", Some("sports"), 27, Some(new Date()), isBill = true)
    val spotify = Transaction("spotify", Some("entertainment"), 10, Some(new Date()), isBill = true)
    val netflix = Transaction("netflix", Some("entertainment"), 10, Some(new Date()), isBill = true)
    val rent = Transaction("rent", Some("living"), 660, Some(new Date()), isBill = true)

    MongoFactory.collection.save(gym)
    MongoFactory.collection.save(swimmingPool)
    MongoFactory.collection.save(spotify)
    MongoFactory.collection.save(netflix)
    MongoFactory.collection.save(rent)
  }

  def postTransaction(transaction: Transaction): WriteResult = {
    logger.info(s"[ ${getCurrentDate()} ] *** Received transation : $transaction . It is to be persisted into the database.")

    val transactionRecord = Common.buildMongoDbObject(transaction)
    val result = MongoFactory.collection.save(transactionRecord)

    logger.info(s"[ ${getCurrentDate()} ] *** Transaction has been persisted.")

    result
  }
}
