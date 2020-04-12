package ru.activity.hub.api.infrastructure

import cats.Functor
import cats.syntax.semigroup._
import http.{ReqCompleter, ResponseObj}
import http.ResponseObj.Just
import http.domain.AccResponse
import com.twitter.finagle.{http => fhttp}
import ru.activity.hub.api.infrastructure.logging.{Logging, LoggingImpl}
import ru.tinkoff.tschema.finagle.tethysInstances.tethysEncodeComplete
import ru.tinkoff.tschema.finagle._
import ru.tinkoff.tschema.finagle.routing.ZioRouting
import tethys.JsonWriter
import zio.ZIO
import zio.interop.catz._

import scala.util.control.NoStackTrace

object HttpTask {
  type HttpTask[A] = ZIO[Context, Throwable, A]
  implicit def logging(implicit source: sourcecode.FullName): Logging[HttpTask] = new LoggingImpl[HttpTask](source.value)

  object HttpContextMissed extends Throwable with NoStackTrace
  case class Rejected(rejection: Rejection) extends Throwable with NoStackTrace

  implicit val stubLift = ZioRouting.zioRouted

  implicit val zioRouted = new RoutedPlus[HttpTask]
  with LiftHttp[HttpTask, HttpTask] {
    implicit private[this] val self: RoutedPlus[HttpTask] = this

    override def matched: HttpTask[Int] =
      ZIO.accessM(
        ctx =>
          ctx.httpContext.fold[HttpTask[Int]](ZIO.fail(HttpContextMissed))(
            httpCtx => ZIO.succeed(httpCtx.matched)))

    override def withMatched[A](m: Int, fa: HttpTask[A]): HttpTask[A] =
      fa.provideSome(ctx =>
        ctx.copy(httpContext = ctx.httpContext.map(_.copy(matched = m))))

    override def path: HttpTask[CharSequence] =
      ZIO.accessM(
        ctx =>
          ctx.httpContext.fold[HttpTask[CharSequence]](
            ZIO.fail(HttpContextMissed))(httpCtx => ZIO.succeed(httpCtx.path)))

    override def request: HttpTask[fhttp.Request] =
      ZIO.accessM(ctx =>
        ctx.httpContext
          .fold[HttpTask[fhttp.Request]](ZIO.fail(HttpContextMissed))(httpCtx =>
            ZIO.succeed(httpCtx.request)))

    override def reject[A](rejection: Rejection): HttpTask[A] =
      Routed
        .unmatchedPath[HttpTask]
        .flatMap(path => throwRej(rejection withPath path.toString))

    override def combineK[A](x: HttpTask[A], y: HttpTask[A]): HttpTask[A] =
      catchRej(x)(xrs => catchRej(y)(yrs => throwRej(xrs |+| yrs)))

    override def apply[A](fa: HttpTask[A]): HttpTask[A] = fa

    @inline private[this] def catchRej[A](z: HttpTask[A])(
        f: Rejection => HttpTask[A]): HttpTask[A] =
      z.catchSome { case Rejected(xrs) => f(xrs) }

    @inline private[this] def throwRej[A](map: Rejection): HttpTask[A] =
      ZIO.fail(Rejected(map))

  }

  implicit val reqCompleter: ReqCompleter[HttpTask] =
    new ReqCompleter[HttpTask] {
      override def apply[F[_]: Functor, A: JsonWriter](
          implicit lift: LiftHttp[HttpTask, F]
      ): Complete[HttpTask, Just[A], F[A]] =
        fa =>
          for {
            a <- lift(fa)
            trackingId <- ZIO.access[Context](_.trackingId)
            res <- tethysEncodeComplete[HttpTask, AccResponse].complete(
              ResponseObj.ok(a, trackingId))
          } yield res
    }

  // you should avoid <+> operator from cats.semigroupk, because you have
  // a risc of usage wrong combineK operation (from zio.catz.interop).
  implicit class SafeCombineK[A](a: HttpTask[A]) {
    def <|>(b: HttpTask[A]): HttpTask[A] = zioRouted.combineK(a, b)
  }

}
