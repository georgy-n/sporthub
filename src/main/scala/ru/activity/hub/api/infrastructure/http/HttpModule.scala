package ru.activity.hub.api.infrastructure.http

import sttp.tapir.Endpoint
import sttp.tapir.server.finatra.{FinatraRoute, FinatraServerOptions}

trait HttpModule {
  def routes(implicit options: FinatraServerOptions): List[FinatraRoute]
  def endPoints: List[Endpoint[_, Unit, _, _]]
}
