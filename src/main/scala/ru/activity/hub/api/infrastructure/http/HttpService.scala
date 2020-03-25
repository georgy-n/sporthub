package ru.activity.hub.api.infrastructure.http

import cats.instances.list._
import cats.syntax.all._
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import ru.activity.hub.api.infrastructure.{Context, HttpContext}
import ru.activity.hub.api.infrastructure.HttpTask.HttpTask
import ru.activity.hub.api.infrastructure.http.Entry.monoid
import zio._
import zio.interop.twitter._
import ru.activity.hub.api.infrastructure.HttpTask._

object HttpService {
  def make(modules: List[HttpModule[HttpTask]])
          (implicit runtime: Runtime[Any]): Service[Request, Response] = {
    val entry     = modules.foldMap(_.entry)
    val fullroute = entry.route

    req => {
//      val requestMeta = requestMetaExtractor.extract(req.headerMap.toMap)
      runtime.unsafeRunToTwitterFuture(for {
        taskCtx  <- ZIO.effect(Context("requestMeta", Some(HttpContext(matched = 0, path = req.path, request = req))))
        response <- fullroute
                      .catchAll(ErrorHandlers.handle)
                      .catchAllCause(
                        cause => {
                          val err = cause.squash
                          ErrorHandlers.handle(err)
                        }
                      ).provide(taskCtx)
      } yield response)
    }
  }
}
