package ru.activity.hub.api.services.user.repo

import ru.activity.hub.api.handlers.users.domain.RegistrationRequest
import ru.activity.hub.api.services.domain.User

trait UserRepository[F[_]] {
  def findUser(userId: User.Id): F[Option[User]]
  def saveUser(req: RegistrationRequest): F[User]
  def findUserByLogin(login: String, password: String): F[Option[User]]
}
