package ru.activity.hub.api.services.user.impl

import cats.MonadError
import cats.syntax.all._
import ru.activity.hub.api.components.handlers.users.domain
import ru.activity.hub.api.components.handlers.users.domain.{Done, LoginRequest, LoginResponse, RegistrationRequest}
import ru.activity.hub.api.infrastructure.exceptions.ServiceError
import ru.activity.hub.api.infrastructure.logging.Logging
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.services.user.UserService
import ru.activity.hub.api.services.user.repo.UserRepository
import ru.activity.hub.api.utils.{SHA256, TokenGenerator}

class UserServiceImpl[F[_]](userRepository: UserRepository[F])(
    implicit me: MonadError[F, Throwable],
    logging: Logging[F]
) extends UserService[F] {
  override def getUser(userId: User.Id): F[User] =
    for {
      maybeUser <- userRepository.findUser(userId)
      user      <- me.fromOption(maybeUser, ServiceError(s"user not found with id=$userId"))
    } yield user

  override def login(req: LoginRequest): F[User] =
    for {
      maybeUser <- userRepository.findUserByLogin(req.login, SHA256.encode(req.password))
      user      <- me.fromOption(maybeUser, ServiceError(s"неправильный логин или пароль"))
    } yield user

  override def registration(req: RegistrationRequest): F[User] = userRepository.saveUser(req)
}
