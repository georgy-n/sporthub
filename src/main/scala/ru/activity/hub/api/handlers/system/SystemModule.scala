package ru.activity.hub.api.handlers.system

import cats.Monad
import cats.effect.std.Dispatcher
import cats.syntax.all._
import ru.activity.BuildInfo
import ru.activity.hub.api.handlers.SecureModule
import ru.activity.hub.api.infrastructure.http._
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.services.domain.User
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.tethysjson.jsonBody
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonReader, JsonWriter}

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

final class SystemModule[F[_]: Monad](
    val sessionManager: SessionManager[F, User.Id],
    rc: ResponseComplete[F]
) extends HttpModule[F] with SecureModule[F] {
  import SystemModule._

  val versionEndpoint: PublicEndpoint[Unit, Unit, Response[VersionInfo], Any] =
    endpoint.get
      .in("version")
      .out(jsonBody[Response[VersionInfo]])

  val versionLogic: F[VersionInfo] = currentVersion.pure[F]

  override def addRoute(builder: ServerBuilder[F]): ServerBuilder[F] =
    builder.addRoute[Unit, Unit, Response[VersionInfo]](
      versionEndpoint,
      _ => rc.complete(versionLogic),
      _ => Monad[F].pure()
    )
}

object SystemModule {
  val startTime =
    ZonedDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMdd_hhmmss"))
  val currentVersion = VersionInfo(
    artifactV = BuildInfo.version,
    version = s"${BuildInfo.gitCurrentBranch}_${BuildInfo.buildTime}",
    branchName = BuildInfo.gitCurrentBranch,
    headCommit = BuildInfo.gitHeadCommit,
    buildTime = BuildInfo.buildTime,
    startTime = startTime
  )

  case class VersionInfo(
      artifactV: String,
      version: String,
      branchName: String,
      headCommit: Option[String],
      buildTime: String,
      startTime: String
  )

  object VersionInfo {
    implicit val versionInfoWriter: JsonWriter[VersionInfo] =
      jsonWriter[VersionInfo]
    implicit val versionInfoReader: JsonReader[VersionInfo] =
      jsonReader[VersionInfo]
  }
}
