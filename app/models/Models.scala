package models

import java.sql.Timestamp

case class Project(id: Long, name: String)

case class Url(id: Long, name: String, address: String, projectId: Long)

case class UrlRecord(id: Long, urlId: Long, createdAt: Timestamp, responseTime: Float, status: String)
