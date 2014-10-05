package models

import java.text.SimpleDateFormat
import java.util.Date

object DateConversors {
  implicit def string2simpleDateFormat(format: String) = new SimpleDateFormat(format)

  implicit def string2date(str: String) = new SimpleDateFormat("MM-dd-yyyy") parse str

  implicit def date2stylishDate(date: Date) = new StylishDate(date)
}

class StylishDate(d: Date) {
  val americanStyle = new SimpleDateFormat("MM-dd-yyyy")

  def inAmericanStyle = inStyle(americanStyle)

  def inStyle(formater: SimpleDateFormat) = formater.format(d)
}