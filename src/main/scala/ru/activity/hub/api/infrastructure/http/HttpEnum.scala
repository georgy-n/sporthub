package ru.activity.hub.api.infrastructure.http

import enumeratum._
import ru.tinkoff.tschema.param.HttpParam
import ru.tinkoff.tschema.swagger.SwaggerTypeable.SwaggerTypeableEnum
import tethys.{JsonReader, JsonWriter}

trait HttpEnum[E <: EnumEntry] extends Enum[E] with SwaggerTypeableEnum[E] with HttpParam.Enum[E] {
  implicit val reader: JsonReader[E] = JsonReader.stringReader.map[E](withName)
  implicit val writer: JsonWriter[E] = JsonWriter.stringWriter.contramap(_.entryName)
}
