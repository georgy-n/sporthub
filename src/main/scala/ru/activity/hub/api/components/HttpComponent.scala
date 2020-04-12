package ru.activity.hub.api.components

import com.twitter.finagle.{Http, ListeningServer, Service}
import ru.activity.hub.api.infrastructure.http.{HttpModule, HttpService}
import zio._
import zio.interop.catz._
import zio.interop.twitter._
import cats.effect.Resource
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.http.{Request, Response}
import ru.activity.hub.api.configs.HttpConfig
import ru.activity.hub.api.infrastructure.HttpTask.HttpTask
import ru.activity.hub.api.infrastructure.HttpTask._
import ru.activity.hub.api.infrastructure.MainTask.MainTask

final case class HttpComponent(public: ListeningServer)

object HttpComponent {
  final case class Modules[F[_]](public: List[HttpModule[F]])

  def build(modules: Modules[HttpTask])(
      config: HttpConfig,
      httpRuntime: Runtime[Any]
  ): Resource[MainTask, HttpComponent] = {
    implicit val r = httpRuntime

    val cors = new Cors.HttpFilter(
      Cors.Policy(
        allowsOrigin = _ => Some("*"),
        allowsMethods = _ => Some(List("GET", "POST", "OPTIONS")),
        allowsHeaders = _ => Some(List("Authorization", "Content-Type", "Accept", "X-Requested-With", "remember-me")),
        exposedHeaders = List()
      )
    )

    def public: Service[Request, Response] =
      HttpService.make(modules.public)

    def bind(server: Http.Server,
             service: Service[Request, Response],
             port: Int): Resource[MainTask, ListeningServer] =
      Resource.make[MainTask, ListeningServer](
        ZIO.effect(server.withNoHttp2.serve(":" + port, cors(_, service)))
      )(ls => Task.fromTwitterFuture(Task.effect(ls.close())))

    for {
      server <- Resource.pure[MainTask, Http.Server](Http.server)
      public <- bind(server, public, config.port)
    } yield HttpComponent(public)
  }
}
