package services

import java.text.SimpleDateFormat
import java.util.Date

import com.mongodb.WriteResult
import com.mongodb.casbah.Imports._
import models.{Common, Transaction}
import grizzled.slf4j.Logging
import marshaller.Marshaller

object POSTHandler extends Marshaller with Logging {
  lazy val loggingDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss")
  def getCurrentDate(): String = loggingDateFormatter.format(new Date())

  def transactionToDBOject(transaction: Transaction): MongoDBObject = Common.buildMongoDbObject(transaction)

  def weeklyExpenses(): Unit = {
    logger.info(s"[ ${getCurrentDate()} ] *** Persisting weekly expenses")

    val train = Transaction("train", Some("transport"), 40, Some(new Date()), isBill = true)

    MongoFactory.collection.save(transactionToDBOject(train))
  }

  def monthlyExpenses(): Unit = {
    logger.info(s"[ ${getCurrentDate()} ] *** Persisting monthly expenses")

    val gym = Transaction("gym", Some("sports"), 25, Some(new Date()), isBill = true)
    val swimmingPool = Transaction("pool", Some("sports"), 27, Some(new Date()), isBill = true)
    val spotify = Transaction("spotify", Some("entertainment"), 10, Some(new Date()), isBill = true)
    val netflix = Transaction("netflix", Some("entertainment"), 10, Some(new Date()), isBill = true)
    val rent = Transaction("rent", Some("living"), 660, Some(new Date()), isBill = true)

    MongoFactory.collection.save(transactionToDBOject(gym))
    MongoFactory.collection.save(transactionToDBOject(swimmingPool))
    MongoFactory.collection.save(transactionToDBOject(spotify))
    MongoFactory.collection.save(transactionToDBOject(netflix))
    MongoFactory.collection.save(transactionToDBOject(rent))
  }

  def postTransaction(transaction: Transaction): WriteResult = {
    logger.info(s"[ ${getCurrentDate()} ] *** Received transation : $transaction . It is to be persisted into the database.")

    val result = MongoFactory.collection.save(transactionToDBOject(transaction))

    logger.info(s"[ ${getCurrentDate()} ] *** Transaction has been persisted.")

    result
  }
}
