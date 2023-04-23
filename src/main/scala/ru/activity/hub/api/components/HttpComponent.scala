package ru.activity.hub.api.components

import cats.effect.unsafe.IORuntime
import cats.effect.{IO, Resource}
import com.twitter.finatra.http.HttpServer
import ru.activity.hub.api.configs.HttpConfig
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.infrastructure.http.{HttpModule, ServerBuilder}
import ru.activity.hub.api.utils.converter._

final case class HttpComponent(public: HttpServer)

object HttpComponent {
  final case class Modules(public: List[HttpModule[MainTask]])

  def build(modules: Modules, config: HttpConfig)(
    implicit runtime: IORuntime
  ): Resource[MainTask, HttpComponent] = {

    def bind(
      endpoints: List[HttpModule[MainTask]],
      port: Int
    ): Resource[MainTask, HttpServer] = {

      val builder: ServerBuilder[MainTask] = ServerBuilder.make
      val customServer = endpoints
        .foldLeft(builder)((builder, module) => module.addRoute(builder))
        .server(port)

      val serverIO = IO
        .delay(customServer.main(Array.empty[String]))
        .map(_ => customServer)
        .to[MainTask]

      Resource.make(serverIO)(serv => serv.close().asF[MainTask])
    }

    for {
      public <- bind(modules.public, config.port)
    } yield HttpComponent(public)
  }

}
