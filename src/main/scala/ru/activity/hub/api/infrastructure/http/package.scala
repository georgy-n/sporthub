package ru.activity.hub.api.infrastructure

import ru.activity.hub.api.infrastructure.Context
import cats.syntax.all._
import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint
import zio.{UIO, ZIO}
import zio.interop.twitter._
import com.twitter.util.{Future => TFuture}
import tethys.JsonWriter
import tethys.jackson._
import tethys._
import tethys.commons.RawJson
package object http {

  implicit class EndpointReacher[I, O](endpoint: Endpoint[I, Unit, Response[O], Any]) {
    def handle(
        logic: I => ZIO[Context, Throwable, O]
    )(runtime: zio.Runtime[Any]): ServerEndpoint[I, Unit, Response[O], Any, TFuture] =
      ServerEndpoint[I, Unit, Response[O], Any, TFuture](
        endpoint,
        _ =>
          i => {
            val r = for {
              trackingId <- ZIO.access[Context](_.trackingId)
              a <- logic(i).either
                .catchAllDefect(th => UIO(Left(th)))
                .flatMap {
                  case Left(value) =>
                    UIO(Response.error(ErrorPayload("msg", "code"), trackingId))
                  case Right(value) => UIO(Response.ok(value, trackingId))
                }
            } yield a
            runtime.unsafeRunToTwitterFuture {
              r.map(a => a.asRight[Unit]).provide(Context(TrackingIdGenerator.generate))
            }
        }
      )
  }
}
