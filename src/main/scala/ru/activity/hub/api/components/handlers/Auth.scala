package ru.activity.hub.api.components.handlers

import cats.Monad
import mouse.all._
import cats.syntax.all._
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.services.domain.User
import ru.tinkoff.tschema.finagle.Authorization.Bearer
import ru.tinkoff.tschema.finagle.{Authorization, BearerToken, LiftHttp, Rejection, Routed}

object Auth {
  implicit def userAuth2[F[_], G[_]: Routed: Monad](
      implicit sessionManager: SessionManager[F, User.Id],
      liftF: LiftHttp[G, F]
  ): Authorization[Bearer, G, User.Id] = {
    case Some(BearerToken(s)) => liftF(sessionManager.get(s))
      .flatMap(uo => uo.cata(_.pure[G], Routed.reject(Rejection.unauthorized)))
    case _ => Routed.reject(Rejection.unauthorized)
  }
}
