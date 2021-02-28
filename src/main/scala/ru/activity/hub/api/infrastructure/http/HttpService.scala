package ru.activity.hub.api.infrastructure.http

import cats.instances.list._
import cats.syntax.all._
//import com.twitter.finagle.Service
//import com.twitter.finagle.http.{Request, Response}
import ru.activity.hub.api.infrastructure.{Context, HttpContext, TrackingIdGenerator}
//import ru.activity.hub.api.infrastructure.http.Entry.monoid
import zio._
import zio.interop.twitter._
import ru.activity.hub.api.infrastructure.logging.Logging

object HttpService {
//  def make(modules: List[HttpModule[HttpTask]])
//          (implicit runtime: Runtime[Any], logging: Logging[HttpTask]): Service[Request, Response] = ???
//  {
//    val entry     = modules.foldMap(_.entry)
//    val fullRoute = entry.route
//
//    req => {
//      val trackingId = TrackingIdGenerator.generate
//      runtime.unsafeRunToTwitterFuture(for {
//        context  <- ZIO.effect(Context(trackingId, Some(HttpContext(matched = 0, path = req.path, request = req))))
//        response <- (logging.info(s"recieved request $req") *> fullRoute
//                      .catchAll(th => ErrorHandlers.handle(th) <* logging.error("error throwed", th))
//                      .catchAllCause(
//                        cause => {
//                          val err = cause.squash
//                          ErrorHandlers.handle(err)
//                        }
//                      )).provide(context)
//      } yield response)
//    }
//  }
}
