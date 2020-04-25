package ru.activity.hub.api.components.handlers.users

import cats.Monad
import cats.effect.Sync
import cats.syntax.all._
import io.scalaland.chimney.dsl._
import ru.activity.hub.api.infrastructure.http.{Entry, HttpModule, ReqCompleter, _}
import ru.activity.hub.api.services.domain.{ShortPersonalInfo, User}
import ru.activity.hub.api.services.user.UserModule
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.utils.TokenGenerator
import ru.tinkoff.tschema.finagle._
import ru.tinkoff.tschema.syntax._
import ru.tinkoff.tschema.finagle.tethysInstances._
import ru.activity.hub.api.infrastructure.NewTypeInstances._

final class UserHandlers[F[_]: Sync, HttpF[_]: Monad: RoutedPlus: LiftHttp[*[_], F]: ReqCompleter](
    userModule: UserModule[F]
)(implicit sm: SessionManager[F, User.Id])
  extends HttpModule[HttpF] {
  import ru.activity.hub.api.components.handlers.Auth.userAuth2
  import ru.activity.hub.api.components.handlers.users.domain._
  import ru.activity.hub.api.components.handlers.tethysRW._

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
        get |>
        operation('shortPersonalInfo) |>
        queryParam[User.Id]("userId") |>
        $$$[ShortPersonalInfo]
      )
      <|>
      (
        post |>
        operation('logout) |>
        bearerAuth[User.Id]('users, 'userId) |>
        $$$[Done]
      )
      <|>
      (
        post |>
        operation('registration) |>
        reqBody[RegistrationRequest] |>
        $$$[User]
      )
    )

  object handler {
    def login(body: LoginRequest): F[LoginResponse] =
      for {
        user <- userModule.userService.login(body)
        session = TokenGenerator.createToken(user)
        _ <- sm.set(session, user.id)
      } yield LoginResponse(session)

    def personalInfo(userId: User.Id): F[User] = userModule.userService.getUser(userId)

    def logout(userId: User.Id): F[Done] = sm.remove(userId).map(_ => Done())

    def registration(body: RegistrationRequest): F[User] = userModule.userService.registration(body)

    def shortPersonalInfo(userId: User.Id): F[ShortPersonalInfo] =
      userModule.userService.getUser(userId).map(_.into[ShortPersonalInfo].transform)
  }
}
