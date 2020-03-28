package ru.activity.hub.api.infrastructure

import io.estatico.newtype.Coercible
import shapeless.LowPriority
import tethys.{JsonReader, JsonWriter}

object NewTypeInstances {
  implicit def NewTypeJsonWriter[R, N](
      implicit
      LP: LowPriority,
      CC: Coercible[JsonWriter[R], JsonWriter[N]],
      R: JsonWriter[R]
  ): JsonWriter[N] = CC(R)

  implicit def NewTypeJsonReader[R, N](
      implicit
      LP: LowPriority,
      CC: Coercible[JsonReader[R], JsonReader[N]],
      R: JsonReader[R]
  ): JsonReader[N] = CC(R)
}
