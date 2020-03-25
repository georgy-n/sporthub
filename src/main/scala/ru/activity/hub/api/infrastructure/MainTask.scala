package ru.activity.hub.api.infrastructure

import ru.activity.hub.api.infrastructure.logging.LoggingImpl
import zio.ZIO
import zio.interop.catz._

object MainTask {
  type MainTask[A] = ZIO[Context, Throwable, A]

  implicit val logging = new LoggingImpl[MainTask]
}
