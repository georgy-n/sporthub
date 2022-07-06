package ru.activity.hub.api.components

import cats.effect.Resource
import com.twitter.finagle.http.filter.Cors
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import ru.activity.hub.api.configs.HttpConfig
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.infrastructure.http.HttpModule
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.server.finatra.{FinatraRoute, FinatraServerOptions, TapirController}
import sttp.tapir.swagger.finatra.SwaggerFinatra
import zio._
import zio.interop.catz._
import zio.interop.twitter._
import com.twitter.util.{Future => TFuture}
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.circe.yaml._
final case class HttpComponent(public: HttpServer)

object HttpComponent {
  final case class Modules(public: List[HttpModule])

  def build(modules: Modules)(
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
//    def failureHandler(response: DefaultDecodeFailureResponse, message: String):DecodeFailureHandling =
//      DecodeFailureHandling.response(failureOutput) {
//        (response, Response(payload = None, resultCode = "Bad", message = Some(message)))
//      }
//    val myDecodeFailureHandler = ServerDefaults.decodeFailureHandler.copy(
//      response = failureHandler,
//      failureMessage = _ => "i dont know"
//    )
    implicit val serveroptions: FinatraServerOptions =
      FinatraServerOptions.default
//        .copy(
//        decodeFailureHandler = ctx => {
//          ctx.input match {
//            case in => DecodeFailureHandling.response(anyJsonBody[Response[Boolean]]) {
//              Response(payload = None, resultCode = "Bad", message = Some(ctx.failure.toString))
//            }
//          }
//        }
//      )

    def bind(
             endpoints: List[HttpModule],
             port: Int): Resource[MainTask, HttpServer] = {
      val yaml: String =
        OpenAPIDocsInterpreter.toOpenAPI(endpoints.flatMap(_.endPoints), "ActivityHub", "1.0").toYaml
      val swagger: SwaggerFinatra = new SwaggerFinatra(yaml)

      val httpServer = server(endpoints.flatMap(_.routes), swagger, port)

      Resource.make[MainTask, HttpServer](
        ZIO.effect(httpServer.main(Array.empty[String])).map(_ => httpServer)
      )(ls => Task.fromTwitterFuture(ls.close()))
    }

    for {
      public <- bind(modules.public, config.port)
    } yield HttpComponent(public)
  }

  private class FinatraController(routers: List[FinatraRoute]) extends Controller with TapirController {
    routers.foreach(addTapirRoute)
  }

  private def server(s: List[FinatraRoute], swagger: SwaggerFinatra, port: Int) = new HttpServer {
    override def defaultHttpPort: String = s":$port"

    protected override def disableAdminHttpServer: Boolean = true

    protected override def configureHttp(router: HttpRouter): Unit = {
      router.add(new FinatraController(s))
      router.add(swagger)
    }
  }

}