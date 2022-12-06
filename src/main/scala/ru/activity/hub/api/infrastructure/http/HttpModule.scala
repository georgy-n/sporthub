package ru.activity.hub.api.infrastructure.http

import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.finatra.{FinatraRoute, FinatraServerOptions}

trait HttpModule[F[_]] {
  def routes(implicit options: FinatraServerOptions): List[FinatraRoute]
  def endPoints: List[Endpoint[_, Unit, _, _]]
  def serverEndpoint: List[ServerEndpoint[_, Unit, _, _, F]]
}
