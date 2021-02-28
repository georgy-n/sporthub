package ru.activity.hub.api.infrastructure.http

import enumeratum._
import tethys.{JsonReader, JsonWriter}

trait HttpEnum[E <: EnumEntry] extends Enum[E] {
  implicit val reader: JsonReader[E] = JsonReader.stringReader.map[E](withName)
  implicit val writer: JsonWriter[E] = JsonWriter.stringWriter.contramap(_.entryName)
}
