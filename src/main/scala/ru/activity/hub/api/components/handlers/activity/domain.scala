package ru.activity.hub.api.components.handlers.activity

import ru.activity.hub.api.services.activity.ActivityService.ActivityOfferRequest
import ru.activity.hub.api.services.activity.domain.{Activity, ActivityStatus, Category}
import tethys.{JsonObjectWriter, JsonReader}
import tethys.derivation.semiauto._

object domain {
  import ActivityStatus._
  import ru.activity.hub.api.infrastructure.NewTypeInstances._

  implicit val activityWriter: JsonObjectWriter[Activity] = jsonWriter[Activity]
  implicit val categoryWriter: JsonObjectWriter[Category] = jsonWriter[Category]
  implicit val activityOfferReader: JsonReader[ActivityOfferRequest] = jsonReader[ActivityOfferRequest]

}
