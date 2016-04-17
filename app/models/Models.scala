package models

import java.sql.Timestamp

case class Project(id: Long, name: String)

case class ProjectFormData(name: String)

case class Url(id: Long, name: String, address: String, projectId: Long, httpMethod: String, body: Option[String])

case class UrlFormData(name: String, address: String, httpMethod: String, body: String)

case class UrlRecord(id: Long, urlId: Long, createdAt: Timestamp, responseTime: Float, status: String)
