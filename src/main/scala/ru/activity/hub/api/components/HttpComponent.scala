package ru.activity.hub.api.components

import cats.data.ReaderT
import cats.effect.{IO, Resource, Sync}
import cats.effect.std.Dispatcher
import cats.effect.unsafe.IORuntime
import com.twitter.finagle.http.filter.Cors
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import ru.activity.hub.api.configs.HttpConfig
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.infrastructure.http.{HttpModule, Response, ServerBuilder}
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.server.finatra.{FinatraRoute, FinatraServerInterpreter, FinatraServerOptions, TapirController}
import com.twitter.util.{Future => TFuture}
import sttp.tapir.docs.openapi._
import sttp.tapir.server.ServerEndpoint
import ru.activity.hub.api.utils.converter._
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler
import sttp.tapir.server.interceptor.reject.DefaultRejectHandler
import sttp.tapir.swagger.SwaggerUI
import ru.activity.hub.api.infrastructure.{Context, MainTask}

final case class HttpComponent(public: HttpServer)

object HttpComponent {
  final case class Modules(public: List[HttpModule[MainTask]])

  def build(
      modules: Modules, config: HttpConfig)(implicit runtime: IORuntime): Resource[MainTask, HttpComponent] = {

    val cors = new Cors.HttpFilter(
      Cors.Policy(
        allowsOrigin = _ => Some("*"),
        allowsMethods = _ => Some(List("GET", "POST", "OPTIONS")),
        allowsHeaders = _ =>
          Some(
            List(
              "Authorization",
              "Content-Type",
              "Accept",
              "X-Requested-With",
              "remember-me"
            )
        ),
        exposedHeaders = List()
      )
    )
//    def failureHandler(response: DefaultDecodeFailureResponse, message: String):DecodeFailureHandling =
//      DecodeFailureHandling.response(failureOutput) {
//        (response, Response(payload = None, resultCode = "Bad", message = Some(message)))
//      }
//    val myDecodeFailureHandler = ServerDefaults.decodeFailureHandler.copy(
//      response = failureHandler,
//      failureMessage = _ => "i dont know"
//    )
    val serveroptions: FinatraServerOptions =
      FinatraServerOptions.default
//        .copy(
//        decodeFailureHandler = ctx => {
//          ctx.failingInput match {
//            case in => DefaultDecodeFailureHandler.default.response(s => Response(payload = None, resultCode = "Bad", message = Some(ctx.failure.toString)) {
//              Response(payload = None, resultCode = "Bad", message = Some(ctx.failure.toString))
//            }
//          }
//        }
//      )
    def bind(
        endpoints: List[HttpModule[MainTask]],
        port: Int
    ): Resource[MainTask, HttpServer] = {

      val builder: ServerBuilder[MainTask] = ServerBuilder.make
      val customServer = endpoints
        .foldLeft(builder)((builder, module) => module.addRoute(builder))
        .server(port)

      val serverIO = IO.delay(customServer.main(Array.empty[String])).map(_ => customServer).to[MainTask]

      Resource.make(serverIO)(serv => serv.close().asF[MainTask])
    }

    for {
      public <- bind(modules.public, config.port)
    } yield HttpComponent(public)
  }



}
