package ru.activity.hub.api.infrastructure.http

import enumeratum.{Enum, EnumEntry}
import tethys.{JsonObjectWriter, JsonReader, JsonWriter}
import tethys.commons.RawJson
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.enumeratum.TethysEnum
import tethys.jackson._
import tethys._

import scala.collection.immutable

case class Response[T](payload: Option[T], trackingId: String, status: Status, errorPayload: Option[ErrorPayload] = None)

object Response {
  implicit def responseWriter[T: JsonWriter]: JsonObjectWriter[Response[T]] = jsonWriter[Response[T]]
  implicit def responseReader[T: JsonReader]: JsonReader[Response[T]]       = jsonReader[Response[T]]

  def make[T](payload: T, status: Status, trackingId: String): Response[T] =
    Response(payload = Some(payload), trackingId = trackingId, status = status, errorPayload = None)

  def error(e: ErrorPayload, trackingId: String): Response[Nothing] =
    Response(None, trackingId, Status.Error, Some(e))

  def ok[R](r: R, trackingId: String): Response[R] =
    make(r, Status.Ok, trackingId)

}

case class ErrorPayload(message: String, code: String, info: Option[Map[String, String]] = None)

object ErrorPayload {
  implicit val errorPayloadWriter: JsonObjectWriter[ErrorPayload] = jsonWriter[ErrorPayload]
  implicit val errorPayloadReader: JsonReader[ErrorPayload]       = jsonReader[ErrorPayload]
}

sealed trait Status extends EnumEntry
object Status extends Enum[Status] with TethysEnum[Status] {
  case object Ok    extends Status
  case object Error extends Status

  override val values: immutable.IndexedSeq[Status] = findValues
}
