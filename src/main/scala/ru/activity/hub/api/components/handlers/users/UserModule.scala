package ru.activity.hub.api.components.handlers.users

import cats.Monad
import ru.activity.hub.api.infrastructure.http._
import ru.activity.hub.api.infrastructure.http.{Entry, HttpModule, ReqCompleter}
import ru.tinkoff.tschema.syntax.{get, _}
import cats.effect.Sync
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.services.user.UserModule
import ru.tinkoff.tschema.finagle.{LiftHttp, MkService, RoutedPlus}
import ru.tinkoff.tschema.syntax._
import tethys._
import tethys.derivation.semiauto._

import ru.activity.hub.api.infrastructure.NewTypeInstances._


final class UserHandlers[
  F[_]: Sync, Http[_]: Monad: RoutedPlus: LiftHttp[*[_], F]: ReqCompleter](userModule: UserModule[F])
  extends HttpModule[Http] {
  import UserEndpointsComponent._

  override val entry = Entry(MkService[Http](api)(handler))

  object handler {
    def info(userId: User.Id): F[User] = userModule.userService.getUser(userId)
  }
}

object UserEndpointsComponent {
  implicit val userWriter: JsonObjectWriter[User] = jsonWriter[User]

  def api =
    prefix('user) |>
      (get |>operation('info) |> queryParam[User.Id]('userId) |> $$$[User])
}

