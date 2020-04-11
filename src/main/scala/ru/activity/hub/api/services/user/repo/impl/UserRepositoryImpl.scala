package ru.activity.hub.api.services.user.repo.impl

import cats.effect.Bracket
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import ru.activity.hub.api.components.handlers.users.domain.RegistrationRequest
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.services.user.repo.UserRepository
import ru.activity.hub.api.infrastructure.DoobieInstances._
import ru.activity.hub.api.utils.SHA256

class UserRepositoryImpl[F[_]](transactor: Transactor[F])(implicit bracket: Bracket[F, Throwable]) extends UserRepository[F]{
  override def findUser(userId: User.Id): F[Option[User]] = {
    sql"select user_id, user_first_name, user_last_name, user_login, user_password from users where user_id=${userId.id}"
      .query[User]
      .option
      .transact(transactor)
  }

  def saveUser(req: RegistrationRequest): F[User] = {
    sql""" INSERT INTO users
           (user_first_name, user_last_name, user_password, user_login, user_role)
           VALUES (${req.firstName}, ${req.secondName}, ${SHA256.encode(req.password)}, ${req.login}, 'USER')
         """
      .update
      .withUniqueGeneratedKeys[User]("user_id", "user_first_name", "user_last_name", "user_login")
      .transact(transactor)
  }

  override def findUserByLogin(login: String, password: String): F[Option[User]] =
    sql"""select
         user_id,
         user_first_name,
         user_last_name,
         user_login,
         user_password
         from users where user_login=$login and user_password=${password}"""
      .query[User]
      .option
      .transact(transactor)
}
