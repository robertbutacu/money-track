package services.transaction

import akka.actor.Actor
import grizzled.slf4j.Logging

class TransactionsPersistanceService extends Actor with Logging {
  override def receive: Receive = {
    case _ => logger.info("Received message in transactions retrieval")
  }
}
