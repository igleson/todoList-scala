package models

import java.text.SimpleDateFormat
import java.util.Date
import javax.persistence._
import javax.validation.constraints.NotNull

import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.db.ebean.Model
import play.db.ebean.Model.Finder

@Entity
class Task() extends Model {

  @Id
  var id: Long = 0l

  @NotNull
  var label: String = null
  var date: Date = null
  var done: Boolean = false

  override def save() = {
    if ("" == label) throw new PersistenceException("EMPTY not allowed for column \"LABEL\"")
    super.save()
  }

  override def toString() = label
}

object Task {

  val DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy")

  val FINDER = new Finder[Long, Task](classOf[Long], classOf[Task])

  implicit val JSON_WRITER = new Writes[Task] {
    override def writes(t: Task): JsValue = {
      Json.obj(
        "id" -> t.id,
        "label" -> t.label,
        "done" -> t.done,
        "date" -> date2String(t.date)
      )
    }
  }

  implicit val JSON_READER: Reads[Task] = (
    (__ \ "label").read[String] and
      (__ \ "done").read[Boolean].orElse(Reads.pure(false)) and
      (__ \ "date").read[String]
    )((label: String, done: Boolean, date: String) => {
    var retorno = new Task()
    retorno.label = label
    retorno.done = done
    retorno.date = DATE_FORMAT.parse(date)
    retorno
  })

  private def date2String(date: Date): String = {
    Option(date) match {
      case Some(date) => DATE_FORMAT.format(date)
      case None => null
    }
  }
}