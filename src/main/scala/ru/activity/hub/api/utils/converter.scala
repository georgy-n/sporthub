package ru.activity.hub.api.utils

import cats.effect.{Async, IO}
import cats.effect.std.Dispatcher
import cats.effect.unsafe.IORuntime

import scala.util.{Failure, Success}
import com.twitter.util.{Future, Promise}

object converter {
  implicit class RichTwitterFuture[A](val f: Future[A]) {
    def asF[F[_] : Async]: F[A] = Async[F].async_ { cb =>
      f.onSuccess(f => cb(Right(f))).onFailure(e => cb(Left(e)))
    }
  }

  implicit class RichF[F[_], A](val fa: F[A]) {
    def asTwitterFuture(implicit dispatcher: Dispatcher[F]): Future[A] = {
      val promise: Promise[A] = new Promise[A]()
      dispatcher
        .unsafeToFuture(fa)
        .onComplete {
          case Success(value) => promise.setValue(value)
          case Failure(exception) => promise.setException(exception)
        }(cats.effect.unsafe.implicits.global.compute)
      promise
    }
  }

  implicit class RichIO[A](val fa: IO[A]) {
    def asTwitterFuture(implicit runtime: IORuntime): Future[A] = {
      val promise: Promise[A] = new Promise[A]()
      fa.unsafeToFuture()
        .onComplete {
          case Success(value) => promise.setValue(value)
          case Failure(exception) => promise.setException(exception)
        }(cats.effect.unsafe.implicits.global.compute)
      promise
    }
  }
}
