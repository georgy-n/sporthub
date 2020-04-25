package ru.activity.hub.api.components.handlers.users

import ru.activity.hub.api.services.domain.ShortPersonalInfo
import tethys.{JsonObjectWriter, JsonReader}
import tethys.derivation.semiauto._

object domain {
  import ru.activity.hub.api.infrastructure.NewTypeInstances._

  case class LoginRequest(login: String, password: String)
  case class LoginResponse(session: String)
  case class RegistrationRequest(login: String, password: String, firstName: String, secondName: String)

  case class Done()

  implicit val registrationReqReader: JsonReader[RegistrationRequest] = jsonReader[RegistrationRequest]
  implicit val loginReqReader: JsonReader[LoginRequest] = jsonReader[LoginRequest]
  implicit val loginRespWriter: JsonObjectWriter[LoginResponse] = jsonWriter[LoginResponse]
  implicit val shortPersonalInfoWriter: JsonObjectWriter[ShortPersonalInfo] = jsonWriter[ShortPersonalInfo]


}
