package ru.activity.hub.api.services.user

import ru.activity.hub.api.services.domain.User

trait UserService[F[_]] {
  def getUser(userId: User.Id): F[User]
}
