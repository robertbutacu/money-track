import java.util.Date

import data.Transaction
import server.{GETHandler, POSTHandler}

object Main extends App {
  POSTHandler.postTransaction(Transaction(Some("test"), Some("test"), 10.0, None))
  GETHandler.getByDay(new Date()).foreach{println}
}
