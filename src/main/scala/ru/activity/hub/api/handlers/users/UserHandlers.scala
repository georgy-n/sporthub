package ru.activity.hub.api.handlers.users

import cats.Monad
import cats.effect.Sync
import cats.syntax.all._
import io.scalaland.chimney.dsl._
import ru.activity.hub.api.infrastructure.http.{HttpModule, _}
import ru.activity.hub.api.services.domain.{ShortPersonalInfo, User}
import ru.activity.hub.api.services.user.UserModule
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.utils.TokenGenerator
import ru.activity.hub.api.infrastructure.NewTypeInstances._
import sttp.tapir.{PublicEndpoint, endpoint, plainBody, stringBody}
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.generic.auto._

class UserHandlers[F[_]: Sync](
  userModule: UserModule[F],
  rc: ResponseComplete[F]
)(implicit sm: SessionManager[F, User.Id])
  extends HttpModule[F] {
  import ru.activity.hub.api.handlers.users.domain._
  import ru.activity.hub.api.handlers.tethysRW._

  val userLogin: PublicEndpoint[LoginRequest, Response[String], Response[LoginResponse], Any] =
    endpoint.post
      .in("user")
      .in("login")
      .in(jsonBody[LoginRequest])
      .out(jsonBody[Response[LoginResponse]])
      .errorOut(jsonBody[Response[String]])

//  def api =
//    prefix('user) |> (
//      (
//        operation('login) |>
//        reqBody[LoginRequest] |>
//        post |>
//        $$$[LoginResponse]
//      )
//      <|>
//      (
//        get |>
//        operation('personalInfo) |>
//        bearerAuth[User.Id]('users, 'userId) |>
//        $$$[User]
//      )
//      <|>
//      (
//        get |>
//        operation('shortPersonalInfo) |>
//        queryParam[User.Id]("userId") |>
//        $$$[ShortPersonalInfo]
//      )
//      <|>
//      (
//        post |>
//        operation('logout) |>
//        bearerAuth[User.Id]('users, 'userId) |>
//        $$$[Done]
//      )
//      <|>
//      (
//        post |>
//        operation('registration) |>
//        reqBody[RegistrationRequest] |>
//        $$$[User]
//      )
//    )

  def addRoute(builder: ServerBuilder[F]): ServerBuilder[F] =
    builder
      .addRoute[LoginRequest, Response[String], Response[LoginResponse]](
        userLogin,
        i => rc.complete(handler.login(i)),
        th => Sync[F].delay(println(th))
          .as(Response.error(ErrorPayload(th.toString, "code for test"), "just for test"))
      )

  object handler {
    def login(body: LoginRequest): F[LoginResponse] = {
      for {
        _ <- Sync[F].delay(println(body))
        user <- userModule.userService.login(body)
        _ <- Sync[F].delay(user)

        session = TokenGenerator.createToken(user)
        _ <- sm.set(session, user.id)
      } yield LoginResponse(session)
    }

    def personalInfo(userId: User.Id): F[User] =
      userModule.userService.getUser(userId)

    def logout(userId: User.Id): F[Done] = sm.remove(userId).map(_ => Done())

    def registration(body: RegistrationRequest): F[User] =
      userModule.userService.registration(body)

    def shortPersonalInfo(userId: User.Id): F[ShortPersonalInfo] =
      userModule.userService
        .getUser(userId)
        .map(_.into[ShortPersonalInfo].transform)
  }
}
