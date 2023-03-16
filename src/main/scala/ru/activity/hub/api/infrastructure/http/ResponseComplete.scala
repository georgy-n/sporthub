package ru.activity.hub.api.infrastructure.http

import ru.activity.hub.api.infrastructure.MainTask
import ru.activity.hub.api.infrastructure.MainTask.MainTask

/*
  Just for transform A to Response[A] without handling errors and etc.
 */
trait ResponseComplete[F[_]] {
  def complete[A](fa: F[A]): F[Response[A]]
}

object ResponseComplete {
  def make: ResponseComplete[MainTask] = new ResponseComplete[MainTask] {
    override def complete[A](fa: MainTask[A]): MainTask[Response[A]] =
      for {
        context <- MainTask.context
        resp    <- fa.map(r => Response.ok(r, context.trackingId))
      } yield resp
  }
}
