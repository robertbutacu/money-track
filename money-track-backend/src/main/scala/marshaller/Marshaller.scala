package marshaller

import models.{Amount, Transaction}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import DateMarshalling._

trait Marshaller extends DefaultJsonProtocol {
  implicit val transactionJson: RootJsonFormat[Transaction] = jsonFormat5(Transaction.apply)
  implicit val amountJson: RootJsonFormat[Amount] = jsonFormat1(Amount.apply)
}
