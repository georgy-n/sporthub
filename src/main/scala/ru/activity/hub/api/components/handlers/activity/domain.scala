package ru.activity.hub.api.components.handlers.activity

import java.time.{Instant, LocalDateTime, ZoneId}

import ru.activity.hub.api.services.activity.ActivityService.{ActivityOfferRequest, Filters}
import ru.activity.hub.api.services.activity.domain.{Activity, ActivityStatus, Category}
import ru.activity.hub.api.utils.Time
import ru.tinkoff.tschema.param.HttpParam
import tethys.{JsonObjectWriter, JsonReader, JsonWriter}
import tethys.derivation.semiauto._

object domain {
  import ActivityStatus._
  import ru.activity.hub.api.infrastructure.NewTypeInstances._

  implicit val dateWriter: JsonObjectWriter[LocalDateTime] = JsonWriter
    .obj[LocalDateTime]
    .addField("seconds")(dt => dt.atZone(ZoneId.systemDefault()).toInstant.getEpochSecond)
  implicit val dateReader: JsonReader[LocalDateTime] = JsonReader.stringReader.map(t =>LocalDateTime.parse(t))
  implicit val activityWriter: JsonObjectWriter[Activity] = jsonWriter[Activity]
  implicit val categoryWriter: JsonObjectWriter[Category] = jsonWriter[Category]
  implicit val activityOfferReader: JsonReader[ActivityOfferRequest] = jsonReader[ActivityOfferRequest]
  implicit val filtersWriter: JsonObjectWriter[Filters] = jsonWriter[Filters]
  implicit val filters: HttpParam[Filters] = HttpParam.generate[Filters]
}
