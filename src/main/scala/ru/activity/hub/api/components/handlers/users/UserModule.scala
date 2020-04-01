package ru.activity.hub.api.components.handlers.users

import cats.Monad
import cats.effect.Sync
import cats.syntax.applicative._
import ru.activity.hub.api.infrastructure.http.{Entry, HttpModule, ReqCompleter, _}
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.services.user.UserModule
import ru.activity.hub.api.infrastructure.NewTypeInstances._
import ru.tinkoff.tschema.finagle.Authorization.Bearer
import ru.tinkoff.tschema.finagle._
import ru.tinkoff.tschema.finagle.util.Unapply
import ru.tinkoff.tschema.syntax._
import ru.tinkoff.tschema.finagle.tethysInstances._

import Serve.bearerAuthServe
import com.twitter.finagle.http.Response
import tethys._
import tethys.derivation.semiauto._
import shapeless.{HNil, Witness}


final class UserHandlers[
  F[_]: Sync, HttpF[_]: Monad: RoutedPlus: LiftHttp[*[_], F]: ReqCompleter](userModule: UserModule[F])
  extends HttpModule[HttpF] {


  implicit val userAuth2: Authorization[Bearer, HttpF, User] = SimpleAuth {
    case BearerToken(users(user)) => user
  }

  override val entry = Entry(MkService[HttpF](api)(handler))

  val stubu = User(User.Id("stub"), "", "", "")

  implicit val userWriter: JsonObjectWriter[User] = jsonWriter[User]

  val users = Unapply(
    Map(
      "123456" -> User(User.Id("1"), "client", "diamond card", "premium subscription")
    )
  )

  def api =
    prefix('user) |>
        (
          operation('auth) |>
          bearerAuth[Option[User]]('users, 'user) |>
          get |>
          $$$[User]
          )

  object handler {
//    def info(userId: User.Id): F[User] = userModule.userService.getUser(userId)
    def auth(user: Option[User]): F[User] = Sync[F].delay(User(User.Id("stub"), "", "", ""))
  }
}

