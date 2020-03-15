package ru.activity.hub.api.infrastructure

import zio.ZIO

object HttpTask {
  type HttpTask[A] = ZIO[Context, Throwable, A]
}
