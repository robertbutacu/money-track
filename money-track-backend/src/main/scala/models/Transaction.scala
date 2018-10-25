package models

import java.util.Date

case class Transaction(name: String, category: Option[String] = None, amount: Double = 0.0, date: Option[Date], isBill: Boolean = false)