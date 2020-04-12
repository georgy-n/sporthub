package ru.activity.hub.api.components.handlers.activity

import ru.activity.hub.api.services.activity.domain.{Activity, ActivityStatus, Category}
import tethys.JsonObjectWriter
import tethys.derivation.semiauto.jsonWriter

object domain {
  import ActivityStatus._
  import ru.activity.hub.api.infrastructure.NewTypeInstances._

  implicit val activityWriter: JsonObjectWriter[Activity] = jsonWriter[Activity]
  implicit val categoryWriter: JsonObjectWriter[Category] = jsonWriter[Category]

}
