package controllers

import java.text.ParseException

import models.Task
import play.api.libs.json.Json
import play.api.mvc._

import scala.collection.JavaConverters._

object Application extends Controller {

  def allTasks() = Action {
    Ok(Json.toJson(Task.FINDER.all().asScala))
  }

  def createTask() = Action(BodyParsers.parse.json) { request =>
    try {
      val placeResult = request.body.validate[Task]
      placeResult.fold(
      errors => {
          BadRequest("ERROS")
        },
      task => {
          task.save()
          Created("/task/" + task.id)
        }
      )
    } catch {
      case ex: ParseException => {
        BadRequest("Date must be on format MM-DD-YYYY")
      }
    }
  }

  def getTask(id: Long) = Action {
    Option(Task.FINDER.byId(id)) match {
      case Some(task) =>  Ok(Json.toJson(task))
      case None => NotFound
    }
  }

}