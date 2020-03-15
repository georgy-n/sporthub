package ru.activity.hub.infrastructure

import zio.ZIO

object MainTask {
  type MainTask[A] = ZIO[Context, Throwable, A]
}
