package models

import java.time.Instant

case class Transaction(name: String,
                       amount: Double = 0.0,
                       category: Option[String] = None,
                       date: Option[Instant] = Option(Instant.now),
                       isBill: Boolean = false)
