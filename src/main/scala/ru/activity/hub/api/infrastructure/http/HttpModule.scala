package ru.activity.hub.api.infrastructure.http

import cats.effect.std.Dispatcher

trait HttpModule[F[_]] {
  def addRoute(builder: ServerBuilder[F]): ServerBuilder[F]
}
