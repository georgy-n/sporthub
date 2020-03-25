package ru.activity.hub.api.infrastructure

import ru.activity.hub.api.infrastructure.logging.MainTaskLogging
import zio.ZIO

object MainTask {
  type MainTask[A] = ZIO[Context, Throwable, A]

  implicit val logging = new MainTaskLogging
}
