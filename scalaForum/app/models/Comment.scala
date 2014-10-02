package models

import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ForeignKey}

case class Comment(id: Long, comment: String)

class CommentTable(tag: Tag) extends Table[Comment](tag, "COMMENT") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def comment = column[String]("comment", O.NotNull)

  def * = (id, comment) <>(Comment.tupled, Comment.unapply)

  def post = foreignKey("POST_FK", id, TableQuery[PostTable])(_.id)
}
