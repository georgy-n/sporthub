package ru.activity.hub.api.infrastructure.http

import enumeratum._
import ru.activity.hub.api.infrastructure.http.domain.AccResponse
import tethys._
import tethys.commons.RawJson
import tethys.jackson._

import scala.collection.immutable

object ResponseObj {
  sealed trait Status extends EnumEntry
  object Status extends HttpEnum[Status] {
    case object Ok extends Status
    case object Error extends Status

    override val values: immutable.IndexedSeq[Status] = findValues
  }


  def make(payload: String, status: Status, trackingId: String): AccResponse =
    AccResponse(
      payload = RawJson(payload),
      trackingId = trackingId,
      status = status
    )

  def error[E: JsonWriter](e: E, trackingId: String): AccResponse =
    make(e.asJson, Status.Error, trackingId)

  def ok[R: JsonWriter](r: R, trackingId: String): AccResponse =
    make(r.asJson, Status.Ok, trackingId)

  // Just is a formal wrapper of some http result. It tells us
  // that result should be wrapped with context information before rendering.
  final abstract class Just[A]
}
