import java.util.Date
import javax.persistence.PersistenceException

import models.Task
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Tests in fake memory DataBase" should {

    "do not persist task with null label" in new WithApplication {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        try {
          val task = new Task
          task save()
          failure("should not be possible save a task without null label")
        } catch {
          case e: PersistenceException => {
            e.getMessage must contain("NULL not allowed for column \"LABEL\"")
          }
        }
      }
    }

    "do not persist task with empty label" in new WithApplication {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        try {
          val task = new Task
          task.label = ""
          task save()
          failure("should not be possible save a task without empty label")
        } catch {
          case e: PersistenceException => {
            e.getMessage must contain("EMPTY not allowed for column \"LABEL\"")
          }
        }
      }
    }

    "persist task" in new WithApplication {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val task = new Task
        task.label = "nova task"
        task save()

        task.id must not be null
        Task.FINDER.all().size() must be equalTo 1
      }
    }

    "update task" in new WithApplication {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        var task = new Task
        task.label = "nova task"
        task save()
        val id = task.id

        task = Task.FINDER byId (id)
        task.label must be equalTo "nova task"

        task.date must be equalTo null
        task.done must be equalTo false

        task.done = true
        task.label = "changing"
        task.date = Task.DATE_FORMAT.parse("10-10-2014")
        task save()

        task = Task.FINDER byId (id)

        task.label must be equalTo "changing"
        Task.DATE_FORMAT format task.date must be equalTo "10-10-2014"
        task.done must be equalTo true
      }
    }

    "remove task" in new WithApplication {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        var task1 = new Task
        task1.label = "nova task1"
        task1 save()
        var task2 = new Task
        task2.label = "nova task2"
        task2 save()
        var task3 = new Task
        task3.label = "nova task3"
        task3 save()
        var task4 = new Task
        task4.label = "nova task4"
        task4 save()
        var task5 = new Task
        task5.label = "nova task5"
        task5 save()

        Task.FINDER findRowCount() must be equalTo 5

        task1 delete()

        Task.FINDER findRowCount() must be equalTo 4
        Task.FINDER.all().asScala must not contain(task1)
      }
    }
  }

  "Tests in real DataBase" should {

    "database is not empty" in new WithApplication {
      running(TestServer(3333)) {
        Task.FINDER.findRowCount() must be greaterThan 0
      }
    }
  }

  "Tests Rest" should {

    "get empty Task List" in new WithApplication {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val task = route(FakeRequest(GET, "/task")).get

        status(task) must equalTo(OK)
        contentType(task) must beSome.which(_ == "application/json")
        contentAsString(task) must be equalTo "[]"
      }
    }

    "create Task" in new WithApplication {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val json = Json.obj(
          "label" -> "test1",
          "date" -> "11-11-1111",
          "done" -> false
        )

        val createTaskRequest = FakeRequest(method = "POST",
          uri = "/task",
          headers = FakeHeaders(
            Seq("Content-type" -> Seq("application/json"))
          ),
          body = json
        )
        val createTask = route(createTaskRequest).get
        val newTaskURI = contentAsString(createTask)

        status(createTask) must equalTo(CREATED)
        contentType(createTask) must beSome.which(_ == "text/plain")
        contentAsString(createTask) must beMatching("/task/\\d*")

        val taskResult = route(FakeRequest(GET, newTaskURI)).get

        status(taskResult) must equalTo(OK)
        contentType(taskResult) must beSome.which(_ == "application/json")
        contentAsJson(taskResult).validate[Task].fold(
          errors => {
            failure("Json is not from a valid task")
          },
          task => {
            task.id must not be null
            task.label must be equalTo "test1"
            Task.DATE_FORMAT format task.date must be equalTo "11-11-1111"
            task.done must be equalTo false
          }
        )
      }
    }

    "get not existent Task" in new WithApplication {
      running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
        val response = route(FakeRequest(GET, "/task/1")).get

        status(response) must equalTo(NOT_FOUND)
      }
    }
  }
}
