package dao

import javax.inject.Inject

import models.Project
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

trait ProjectsComponent { self: HasDatabaseConfigProvider[JdbcProfile] =>
  import driver.api._

  class Projects(tag: Tag) extends Table[Project](tag, "PROJECT") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")

    def * = (id, name) <> (Project.tupled, Project.unapply _)
  }
}

class ProjectDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends ProjectsComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val projects = TableQuery[Projects]

  def all(): Future[Seq[Project]] = db.run(projects.result)

  def delete(id: Long): Future[Unit] =
    db.run(projects.filter(_.id === id).delete).map(_ => ())

  def insert(project: Project): Future[Unit] =
    db.run(projects += project).map(_ => ())
}
