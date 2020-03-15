package ru.activity.hub.api.infrastructure

import zio.ZIO

object MainTask {
  type MainTask[A] = ZIO[Context, Throwable, A]
}
