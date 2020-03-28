package ru.activity.hub.api.services.user.impl

import cats.MonadError
import cats.syntax.all._
import ru.activity.hub.api.infrastructure.exceptions.ServiceError
import ru.activity.hub.api.infrastructure.logging.Logging
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.services.user.UserService
import ru.activity.hub.api.services.user.repo.UserRepository

class UserServiceImpl[F[_]](
    userRepository: UserRepository[F])(implicit me: MonadError[F, Throwable], logging: Logging[F])
    extends UserService[F] {
  override def getUser(userId: User.Id): F[User] = {
    for {
      _ <- logging.info("in service")
      maybeUser <- userRepository.findUser(userId)
      _ <- logging.info(s"$maybeUser")
      user <- me.fromOption(maybeUser,ServiceError(s"user not found with id=$userId"))

    } yield user
  }
}
