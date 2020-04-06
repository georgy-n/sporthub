package ru.activity.hub.api.components

import cats.effect.{Resource, Sync}
import cats.effect.Resource.liftF
import cats.syntax.all._
import ru.activity.hub.api.infrastructure.logging.Logging
import ru.activity.hub.api.infrastructure.session.{SessionManager, SessionManagerImpl}
import ru.activity.hub.api.services.domain.User

case class SessionComponent[F[_], T](manager: SessionManager[F, T])


object SessionComponent {
  def build[F[_]: Sync](implicit logg: Logging[F]): Resource[F, SessionManager[F, User.Id]] = liftF {
    for {
      sessionManager <- Sync[F].delay(new SessionManagerImpl[F, User.Id])
    _ <- sessionManager.set("123", User.Id("1"))
    } yield sessionManager
  }
}