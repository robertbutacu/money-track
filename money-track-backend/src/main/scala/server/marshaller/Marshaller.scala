package server.marshaller

import data.{Amount, Transaction}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import server.marshaller.DateMarshalling._

trait Marshaller extends DefaultJsonProtocol {
  implicit val transactionJson: RootJsonFormat[Transaction] = jsonFormat5(Transaction.apply)
  implicit val amountJson: RootJsonFormat[Amount] = jsonFormat1(Amount.apply)
}
