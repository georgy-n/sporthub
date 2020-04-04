package ru.activity.hub.api.components.handlers.users

import cats.Monad
import cats.effect.Sync
import ru.activity.hub.api.infrastructure.http.{Entry, HttpModule, ReqCompleter, _}
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.services.user.UserModule
import ru.activity.hub.api.infrastructure.NewTypeInstances._
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.tinkoff.tschema.finagle._
import ru.tinkoff.tschema.syntax._
import tethys._
import tethys.derivation.semiauto._


final class UserHandlers[
  F[_]: Sync,
  HttpF[_]: Monad: RoutedPlus: LiftHttp[*[_], F]: ReqCompleter: SessionManager[*[_], User]
](userModule: UserModule[F])
  extends HttpModule[HttpF] {
  import ru.activity.hub.api.components.handlers.Auth.userAuth2

  override val entry = Entry(MkService[HttpF](api)(handler))


  implicit val userWriter: JsonObjectWriter[User] = jsonWriter[User]


  def api =
    prefix('user) |>
        (
          get |>
          operation('auth) |>
          bearerAuth[Option[User]]('users, 'user) |>
          $$$[User]
          )

  object handler {
//    def info(userId: User.Id): F[User] = userModule.userService.getUser(userId)
    def auth(user: Option[User]): F[User] = Sync[F].delay(User(User.Id("stub"), "", "", ""))
  }
}

