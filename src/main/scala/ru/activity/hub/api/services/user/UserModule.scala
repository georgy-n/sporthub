package ru.activity.hub.api.services.user

import cats.effect.Sync
import cats.syntax.all._
import doobie.util.transactor.Transactor
import ru.activity.hub.api.infrastructure.logging.Logging
import ru.activity.hub.api.services.user.impl.UserServiceImpl
import ru.activity.hub.api.services.user.repo.impl.UserRepositoryImpl

case class UserModule[F[_]](userService: UserService[F])

object UserModule {
  def build[F[_]](transactor: Transactor[F])(
      implicit sync: Sync[F],
      logging: Logging[F]): F[UserModule[F]] = {
    for {
      _ <- logging.info("init User module")
      userRepo = new UserRepositoryImpl[F](transactor)
      userService = new UserServiceImpl[F](userRepo)
      _ <- logging.info("inited User module")

    } yield UserModule(userService)
  }
}
