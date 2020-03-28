package ru.activity.hub.api.components

import cats.effect.Sync
import cats.syntax.all._
import ru.activity.hub.api.infrastructure.logging.Logging
import ru.activity.hub.api.services.user.UserModule

case class ServicesComponent[F[_]](userModule: UserModule[F])

object ServicesComponent {
  def build[F[_]](dbComponent: DatabaseComponent[F])(
    implicit
    sync: Sync[F],
    logging: Logging[F]
  ): F[ServicesComponent[F]] = {
    for {
      _ <- logging.info("init Services module")
      userModule <- UserModule.build[F](dbComponent.db)
      _ <- logging.info("inited Services module")

    } yield ServicesComponent(userModule)
  }
}
