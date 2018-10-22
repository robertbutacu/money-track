package server.marshaller

import data.Transaction
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import server.marshaller.DateMarshalling._

trait Marshaller extends DefaultJsonProtocol {
  implicit val transactionJson: RootJsonFormat[Transaction] = jsonFormat5(Transaction.apply)
}
