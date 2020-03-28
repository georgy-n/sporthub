package ru.activity.hub.api.infrastructure

import cats.Functor
import ru.activity.hub.api.infrastructure.http.ResponseObj.Just
import ru.tinkoff.tschema.typeDSL.Complete
import tethys.JsonWriter
import ru.tinkoff.tschema.finagle.{LiftHttp, Complete => FComplete}

package object http {
  def $$$[A]: Complete[Just[A]] = new Complete

  trait ReqCompleter[Http[_]] {
    def apply[F[_]: Functor, A: JsonWriter](
        implicit lift: LiftHttp[Http, F]
    ): FComplete[Http, Just[A], F[A]]
  }

  implicit def JustCompleteFromReqCompleter[Http[_],
                                            A: JsonWriter,
                                            F[_]: Functor](
      implicit
      completer: ReqCompleter[Http],
      lift: LiftHttp[Http, F]
  ): FComplete[Http, Just[A], F[A]] =
    completer.apply

}
