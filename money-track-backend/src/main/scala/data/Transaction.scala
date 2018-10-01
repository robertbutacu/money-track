package data

import java.util.Date

case class Transaction(name: String, category: Option[String] = None, amount: BigDecimal, date: Date)
