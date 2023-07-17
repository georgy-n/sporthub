package ru.activity.hub.api.handlers

import ru.activity.hub.api.handlers.users.domain.Done
import ru.activity.hub.api.services.domain.User
import tethys.{JsonObjectWriter, JsonReader}
import tethys.derivation.semiauto.{jsonReader, jsonWriter}

object tethysRW {
  import ru.activity.hub.api.infrastructure.NewTypeInstances._

  implicit val doneWriter: JsonObjectWriter[Done] = jsonWriter[Done]

  implicit val userWriter: JsonObjectWriter[User] = jsonWriter[User]
  implicit val userReader: JsonReader[User] = jsonReader[User]
}
