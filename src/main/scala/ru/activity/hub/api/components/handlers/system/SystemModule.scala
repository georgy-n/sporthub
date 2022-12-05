package ru.activity.hub.api.components.handlers.system

import sttp.tapir.Endpoint
import sttp.tapir.server.finatra.{FinatraRoute, FinatraServerInterpreter, FinatraServerOptions}

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import ru.activity.BuildInfo
import ru.activity.hub.api.infrastructure.http.Response
import ru.activity.hub.api.infrastructure.http._
import ru.activity.hub.api.infrastructure.http.HttpModule
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.ztapir.endpoint
import tethys.{JsonReader, JsonWriter}
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import com.twitter.util.{Future => TFuture}
import zio.interop.twitter._
import cats.syntax.all._
import zio.{UIO, ZIO}
import sttp.tapir.ztapir._
import sttp.tapir.generic.auto._

final class SystemModule(runtime: zio.Runtime[Any]) extends HttpModule {
  import SystemEndpointsComponent._

  val versionEndpoint =
    endpoint.get
      .in("version")
      .out(jsonBody[Response[VersionInfo]])
      .handle(_ => UIO(currentVersion))(runtime)

  override def routes(implicit options: FinatraServerOptions): List[FinatraRoute] =
    List(versionEndpoint).map(FinatraServerInterpreter.toRoute(_)(options))

  def endPoints: List[Endpoint[_, Unit, _, _]] = List(versionEndpoint.endpoint)
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

  object VersionInfo {
    implicit val versionInfoWriter: JsonWriter[VersionInfo] = jsonWriter[VersionInfo]
    implicit val versionInfoReader: JsonReader[VersionInfo] = jsonReader[VersionInfo]
  }
}
