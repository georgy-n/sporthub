package ru.activity.hub.api.components.handlers.users

import tethys.{JsonObjectWriter, JsonReader}
import tethys.derivation.semiauto._

object domain {
  case class LoginRequest(login: String, password: String)
  case class LoginResponse(session: String)
  case class RegistrationRequest(login: String, password: String, firstName: String, secondName: String)

  implicit val registrationReqReader: JsonReader[RegistrationRequest] = jsonReader[RegistrationRequest]
  implicit val loginReqReader: JsonReader[LoginRequest] = jsonReader[LoginRequest]
  implicit val loginRespWriter: JsonObjectWriter[LoginResponse] = jsonWriter[LoginResponse]

}
