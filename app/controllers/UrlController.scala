package controllers

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Date
import javax.inject.Inject

import dao._
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws.{WSResponse, WSClient}
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class UrlController @Inject() (urlDao: UrlDao, urlRecordDao: UrlRecordDao, ws: WSClient) extends Controller {

  case class UrlFormData(name: String, address: String)

  val urlForm = Form(
    mapping(
      "name" -> text(),
      "address" -> text()
    )(UrlFormData.apply)(UrlFormData.unapply)
  )

  def deleteUrl(projectId: Long, urlId: Long) = Action.async { implicit request =>
    urlDao.delete(urlId).map(_ => Redirect(routes.ProjectController.listProjects))
  }

  def showCreateUrlFormView(projectId: Long) = Action {
    Ok(views.html.createurl(projectId))
  }

  def insertUrl(projectId: Long) = Action.async { implicit request =>
    val urlFormData: UrlFormData = urlForm.bindFromRequest.get
    val url = Url(0, urlFormData.name, urlFormData.address, projectId)
    urlDao.insert(url).map(_ => Redirect(routes.UrlController.listUrls(projectId)))
  }

  private def getData(url: Url): Future[UrlRecord] = {
    val start = System.nanoTime()
    ws.url(url.address).get().map {
      response =>
        val end = System.nanoTime()
        UrlRecord(0, url.id, Timestamp.valueOf(LocalDateTime.now()), (end - start) / 1000 / 1000, response.statusText)
    }
  }

  def fetchUrls (projectId: Long) = Action.async { implicit request =>
    val urlsFuture: Future[Seq[Url]] = urlDao.findByProjectId(projectId)
    val urlRecordsFuture: Future[Seq[UrlRecord]] = urlsFuture.flatMap(us => Future.traverse(us)(getData))
    val result = urlRecordsFuture.map(urs => urs.map(ur => urlRecordDao.insert(ur)))
    result.map(_ => Redirect(routes.UrlController.listUrls(projectId)))
  }

  def listUrls(projectId: Long) = Action.async { implicit request =>
    urlDao.findByProjectId(projectId).map(u => Ok(views.html.urls(projectId, u)))
  }

  def listUrlRecords(projectId: Long, urlId: Long) = Action.async { implicit request =>
    urlRecordDao.findByUrlId(urlId).map(u => Ok(views.html.urlrecords(projectId, urlId, u)))
  }
}
