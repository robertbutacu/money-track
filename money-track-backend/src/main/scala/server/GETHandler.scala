package server

import java.util.Date

import data.Transaction

object GETHandler {
  def getByDay(day: Date): List[Transaction] = ???
  def getByPeriod(start: Date, end: Date): List[Transaction] = ???

  def getAmountSpentByDate(day: Date): BigDecimal = ???
  def getAmountSpentByPeriod(start: Date, end: Date): BigDecimal = ???
}
