package controllers

import javax.inject.Inject

import dao._
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class ProjectController @Inject() (val messagesApi: MessagesApi, projectDao: ProjectDao) extends Controller with I18nSupport {

  val projectForm = Form(
    mapping(
      "name" -> text
    )(ProjectFormData.apply)(ProjectFormData.unapply)
  )

  def listProjects = Action.async {
    projectDao.all().map(p => Ok(views.html.index(p)))
  }

  def showCreateProjectFormView = Action {
    Ok(views.html.createproject(projectForm))
  }

  def insertProject = Action.async { implicit request =>
    val projectFormData: ProjectFormData = projectForm.bindFromRequest.get
    val project = Project(0, projectFormData.name)
    projectDao.insert(project).map(_ => Redirect(routes.ProjectController.listProjects))
  }

  def deleteProject(projectId: Long) = Action.async { implicit request =>
    projectDao.delete(projectId).map(_ => Redirect(routes.ProjectController.listProjects))
  }

}
