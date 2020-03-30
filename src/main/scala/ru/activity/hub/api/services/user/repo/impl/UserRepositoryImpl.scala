package ru.activity.hub.api.services.user.repo.impl

import cats.effect.Bracket
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.services.user.repo.UserRepository
import ru.activity.hub.api.infrastructure.DoobieInstances._

class UserRepositoryImpl[F[_]](transactor: Transactor[F])(implicit bracket: Bracket[F, Throwable]) extends UserRepository[F]{
  override def findUser(userId: User.Id): F[Option[User]] = {
    sql"select id, first_name, last_name, user_name from users where id=${userId.id}"
      .query[User]
      .option
      .transact(transactor)
  }
}
