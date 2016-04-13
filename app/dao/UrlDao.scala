package dao

import javax.inject.Inject

import models.Url
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile

import scala.concurrent.Future

class UrlDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends ProjectsComponent
  with HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val urls = TableQuery[Urls]

  def findByProjectId(projectId: Long): Future[Seq[Url]] =
    db.run(urls.filter(_.projectId === projectId).result)

  def insert(url: Url): Future[Unit] = {
    db.run(urls += url).map(_ => ())
  }

  def delete(id: Long): Future[Unit] =
    db.run(urls.filter(_.id === id).delete).map(_ => ())

  class Urls(tag: Tag) extends Table[Url](tag, "URL") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def address = column[String]("ADDRESS")
    def projectId = column[Long]("PROJECT_ID")

    def * = (id, name, address, projectId) <>(Url.tupled, Url.unapply _)
  }

}
