package ru.activity.hub.api.services.user.repo

import ru.activity.hub.api.services.domain.User

trait UserRepository[F[_]] {
  def findUser(userId: User.Id): F[Option[User]]
}
