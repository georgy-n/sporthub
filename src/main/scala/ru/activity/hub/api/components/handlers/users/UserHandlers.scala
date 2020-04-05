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
import ru.tinkoff.tschema.finagle.tethysInstances._

import tethys._
import tethys.derivation.semiauto._

final class UserHandlers[
    F[_]: Sync,
    HttpF[_]: Monad: RoutedPlus: LiftHttp[*[_], F]: ReqCompleter: SessionManager[*[_], User.Id]
](userModule: UserModule[F])
    extends HttpModule[HttpF] {
  import ru.activity.hub.api.components.handlers.Auth.userAuth2
  import ru.activity.hub.api.components.handlers.users.domain._

  implicit val userWriter: JsonObjectWriter[User] = jsonWriter[User]
  implicit val userReader: JsonReader[User] = jsonReader[User]

  override val entry = Entry(MkService[HttpF](api)(handler))

  def api =
    prefix('user) |> (
      (
        operation('login) |>
          reqBody[LoginRequest] |>
          post |>
          $$$[LoginResponse]
      )
        <|>
        (
          get |>
            operation('personalInfo) |>
            bearerAuth[User.Id]('users, 'userId) |>
            $$$[User]
        )
        <|>
        (
          post |>
            operation('logout) |>
            bearerAuth[User.Id]('users, 'userId) |>
            $$$[String]
          )
        <|>
        (
          post |>
            operation('registration) |>
            reqBody[RegistrationRequest] |>
            $$$[String]
          )
    )

  object handler {
    def login(body: LoginRequest): F[LoginResponse] = ???
    def personalInfo(userId: User.Id): F[User] = ???
    def logout(userId: User.Id): F[String] = ???
    def registration(body: RegistrationRequest): F[String] = ???
  }
}
