package ru.activity.hub.api.infrastructure

import ru.activity.hub.api.infrastructure.logging.{Logging, LoggingImpl}
import zio.ZIO
import zio.interop.catz._

object MainTask {
  type MainTask[A] = ZIO[Context, Throwable, A]

  implicit def logging(implicit source: sourcecode.FullName): Logging[MainTask] = new LoggingImpl[MainTask](source.value)
}
