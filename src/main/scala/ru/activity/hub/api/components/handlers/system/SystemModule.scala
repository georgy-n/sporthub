package ru.activity.hub.api.components.handlers.system

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

import buildinfo.BuildInfo
import cats.Monad
import cats.effect.Sync
import ru.activity.hub.api.infrastructure.http._
import ru.activity.hub.api.infrastructure.http.{Entry, HttpModule, ReqCompleter}
import ru.tinkoff.tschema.finagle.{LiftHttp, MkService, RoutedPlus}
import ru.tinkoff.tschema.swagger.SwaggerBuilder
import ru.tinkoff.tschema.syntax._
import tethys.JsonObjectWriter
import tethys.derivation.semiauto.{jsonReader, jsonWriter}

final class SystemModule[
    F[_]: Sync, Http[_]: Monad: RoutedPlus: LiftHttp[*[_], F]: ReqCompleter]
    extends HttpModule[Http] {
  import SystemEndpointsComponent._

  override val entry = Entry(MkService[Http](api)(handler))

  object handler {
    val version = Sync[F].pure(currentVersion)
  }
}

object SystemEndpointsComponent {
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

  implicit val vW: JsonObjectWriter[VersionInfo] = jsonWriter[VersionInfo]

  def api =
    tag('System) |> (get |> prefix('system) |> (
      (operation('version) |> $$$[VersionInfo])
    ))
}
