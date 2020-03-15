package ru.activity.hub.infrastructure

import zio.ZIO

object HttpTask {
  type HttpTask[A] = ZIO[Context, Throwable, A]
}
