package ru.activity.hub.api.handlers

import cats.Monad
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.services.domain.User
import sttp.tapir._
import cats.syntax.all._
import cats.instances.all._

import sttp.tapir.server.PartialServerEndpoint

trait SecureModule[F[_]] {

//  def sessionManager: SessionManager[F, User.Id]
//
//  private def authorize(session: String): F[User.Id] = sessionManager.foo(session)
//
//  val secureEndpoint: PartialServerEndpoint[User.Id, Unit, Throwable, Unit, Nothing, F] = endpoint
//    .in(header[String]("Authorization"))
//    .in("api" / "1.0")
//    .errorOut(stringBody.map(s => new Throwable(s))(_.getMessage))
//    .serverLogicForCurrentRecoverErrors(a => authorize(a))
}
