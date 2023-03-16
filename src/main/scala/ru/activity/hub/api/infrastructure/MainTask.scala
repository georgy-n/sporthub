package ru.activity.hub.api.infrastructure

import cats.data.{ReaderT, StateT}
import cats.effect.IO
import ru.activity.hub.api.infrastructure.logging.{Logging, LoggingImpl}

object MainTask {
  type MainTask[A] = ReaderT[IO, Context, A]

  def context: MainTask[Context] = ReaderT.ask[IO, Context]

  implicit def logging(implicit source: sourcecode.FullName): Logging[MainTask] = new LoggingImpl[MainTask](source.value)
}
