package ru.activity.hub.api.infrastructure.http

import ru.activity.hub.api.infrastructure.http.ResponseObj.Status
import tethys.JsonObjectWriter
import tethys.commons.RawJson
import tethys.derivation.semiauto.jsonWriter

object domain {

  case class AccResponse(
                          payload: RawJson,
                          trackingId: String, // TODO newtype this
                          status: Status
                        )

  case class ErrorPayload(
                           message: String,
                           code: String,
                           info: Option[Map[String, String]] = None
                         )

  implicit val rW: JsonObjectWriter[AccResponse] = jsonWriter[AccResponse]
  implicit val eW: JsonObjectWriter[ErrorPayload] = jsonWriter[ErrorPayload]

}
