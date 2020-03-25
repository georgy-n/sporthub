package ru.activity.hub.api.infrastructure

import cats.Functor
import ru.tinkoff.tschema.finagle.LiftHttp
import ru.tinkoff.tschema.typeDSL.Complete
import tethys.JsonWriter
import cats.Functor
import ru.activity.hub.api.infrastructure.http.ResponseObj.Just
import ru.tinkoff.tschema.finagle.LiftHttp
import ru.tinkoff.tschema.typeDSL.Complete
import tethys.JsonWriter
import ru.tinkoff.tschema.finagle.{LiftHttp, Complete => FComplete}

package object http {
  def $$$[A]: Complete[Just[A]] = new Complete

  // Enriches wrapper with error case

  // ReqCompleter constraints context with requirement of definition 'how exactly' `Just` should be constructed.
  // It should be defined in Http task, since it may (and will) interact with its context.
  // See also `Just`
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
