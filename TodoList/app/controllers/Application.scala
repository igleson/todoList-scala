package controllers

import java.text.ParseException
import javax.persistence.PersistenceException

import models.Task
import play.api.libs.json.Json
import play.api.mvc._

import scala.collection.JavaConverters._

object Application extends Controller {
  def allTasks() = Action {
    Ok(Json.toJson(Task.finder.all().asScala))
  }

  def createTask() = Action(BodyParsers.parse.json) { request =>
    try {
      val placeResult = request.body.validate[Task]
      placeResult.fold(
        errors => {
          BadRequest("ERROR")
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
      case ex: PersistenceException => {
        BadRequest(ex.getMessage())
      }
    }
  }

  def getTask(id: Long) = Action {
    Option(Task.finder.byId(id)) match {
      case Some(task) => Ok(Json.toJson(task))
      case None => NotFound
    }
  }

  def list() = Action {
    Ok(views.html.index.render())
  }

  def index() = Action {
    Redirect(routes.Application.list())
  }
}