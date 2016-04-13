package dao

import java.sql.Timestamp
import javax.inject.Inject

import models.UrlRecord
import play.api.db.slick.{HasDatabaseConfigProvider, DatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


/**
  * Created by ozan on 3/22/16.
  */
class UrlRecordDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val urlRecords = TableQuery[UrlRecords]

  def findByUrlId(urlId: Long): Future[Seq[UrlRecord]] =
    db.run(urlRecords.filter(_.urlId === urlId).result)

  def insert(urlRecord: UrlRecord): Future[Unit] =
    db.run(urlRecords += urlRecord).map(_ => ())

  class UrlRecords(tag: Tag) extends Table[UrlRecord](tag, "URL_RECORD") {
    def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
    def urlId = column[Long]("URL_ID")
    def createdAt = column[Timestamp]("CREATED_AT")
    def responseTime = column[Float]("RESPONSE_TIME")
    def status = column[String]("STATUS")
    def * = (id, urlId, createdAt, responseTime, status) <> (UrlRecord.tupled, UrlRecord.unapply _)
  }
}
