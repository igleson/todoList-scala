package models

import models.DateConversors._

import java.util.Date
import javax.persistence._
import javax.validation.constraints.NotNull

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.db.ebean.Model
import play.db.ebean.Model.Finder

@Entity
class Task extends Model {

  @Id
  var id: Long = _

  @NotNull
  var label: String = _
  var date: Date = _
  var done: Boolean = _

  override def save = {
    if ("" == label) throw new PersistenceException("EMPTY not allowed for column \"LABEL\"")
    super.save()
  }

  override def toString = label
}

object Task {
  val finder = new Finder[Long, Task](classOf[Long], classOf[Task])

  implicit def task2json = new Writes[Task] {
    override def writes(t: Task) = {
      Json.obj(
        "id" -> t.id,
        "label" -> t.label,
        "done" -> t.done,
        "date" -> t.date.inAmericanStyle
      )
    }
  }

  implicit def json2task = (
    (__ \ "label").read[String] and
      (__ \ "done").read[Boolean].orElse(Reads.pure(false)) and
      (__ \ "date").read[String]
    )((label: String, done: Boolean, date: String) => {
    val ret = new Task
    ret.label = label
    ret.done = done
    ret.date = date
    ret
  })
}
