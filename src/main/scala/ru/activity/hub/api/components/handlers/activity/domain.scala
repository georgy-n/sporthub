package ru.activity.hub.api.components.handlers.activity

import java.time.{LocalDateTime, ZoneId}

import ru.activity.hub.api.services.activity.ActivityService.{ActivityOfferRequest, EditActivityRequest, Filters, SetCommentRequest}
import ru.activity.hub.api.services.activity.domain.{Activity, ActivityInfo, ActivityStatus, Category, Comment}
import ru.tinkoff.tschema.param.HttpParam
import tethys.{JsonObjectWriter, JsonReader, JsonWriter}
import tethys.derivation.semiauto._

object domain {
  import ActivityStatus._
  import ru.activity.hub.api.infrastructure.NewTypeInstances._

  case class DeleteActivityRequest(id: Activity.Id)
  implicit val dateWriter: JsonObjectWriter[LocalDateTime] = JsonWriter
    .obj[LocalDateTime]
    .addField("seconds")(dt => dt.atZone(ZoneId.systemDefault()).toInstant.getEpochSecond)
  implicit val dateReader: JsonReader[LocalDateTime] = JsonReader.stringReader.map(t =>LocalDateTime.parse(t))
  implicit val activityWriter: JsonObjectWriter[Activity] = jsonWriter[Activity]

  implicit val activityInfoWriter: JsonObjectWriter[ActivityInfo] = jsonWriter[ActivityInfo]
  implicit val categoryWriter: JsonObjectWriter[Category] = jsonWriter[Category]
  implicit val activityOfferReader: JsonReader[ActivityOfferRequest] = jsonReader[ActivityOfferRequest]
  implicit val activityEditReader: JsonReader[EditActivityRequest] = jsonReader[EditActivityRequest]
  implicit val activityDeleteReader: JsonReader[DeleteActivityRequest] = jsonReader[DeleteActivityRequest]

  implicit val filtersWriter: JsonObjectWriter[Filters] = jsonWriter[Filters]
  implicit val filters: HttpParam[Filters] = HttpParam.generate[Filters]
  implicit val commentWriter: JsonObjectWriter[Comment] = jsonWriter[Comment]
  implicit val setCommentReader: JsonReader[SetCommentRequest] = jsonReader[SetCommentRequest]
}
