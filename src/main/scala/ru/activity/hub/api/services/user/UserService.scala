package ru.activity.hub.api.services.user

import ru.activity.hub.api.components.handlers.users.domain.{Done, LoginRequest, LoginResponse, RegistrationRequest}
import ru.activity.hub.api.services.domain.User

trait UserService[F[_]] {
  def getUser(userId: User.Id): F[User]
  def login(req: LoginRequest): F[User]
  def registration(req: RegistrationRequest): F[User]
}
