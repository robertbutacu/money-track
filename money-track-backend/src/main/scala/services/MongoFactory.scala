package services

import com.mongodb.casbah.MongoConnection

object MongoFactory {
  private val SERVER = "localhost"
  private val DATABASE = "money-track"
  private val COLLECTION = "transactions"
  val connection = MongoConnection(SERVER)
  val collection = connection(DATABASE)(COLLECTION)
}
