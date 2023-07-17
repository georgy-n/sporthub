package ru.activity.hub.api.infrastructure.http

import cats.effect.unsafe.IORuntime
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.finatra.http.routing.HttpRouter
import sttp.tapir.{AnyEndpoint, Endpoint, stringBody}
import sttp.tapir.server.finatra.{FinatraRoute, FinatraServerInterpreter, FinatraServerOptions, TapirController}
import cats.syntax.all._
import com.twitter.finagle.http.filter.Cors
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.utils.converter._
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.swagger.SwaggerUI
import com.twitter.util.{Future => TFuture}
import ru.activity.hub.api.infrastructure.{Context, TrackingIdGenerator}
import sttp.tapir.server.interceptor.decodefailure.DefaultDecodeFailureHandler
import sttp.tapir.server.interceptor.exception.ExceptionHandler
import sttp.tapir.server.interceptor.reject.RejectHandler
import sttp.tapir.server.model.ValuedEndpointOutput

trait ServerBuilder[F[_]] {
  def addRoute[I, E, O](
      endpoint: Endpoint[Unit, I, E, O, Any],
      logic: I => F[O],
      errorTr: Throwable => F[E]
  ): ServerBuilder[F]

  def getEndpoints: List[AnyEndpoint]
  def server(port: Int): HttpServer
}

object ServerBuilder {
  def make(implicit runtime: IORuntime): ServerBuilder[MainTask] =
    new ServerBuilderImpl(Nil, Nil)

  class ServerBuilderImpl(
      routes: List[FinatraRoute],
      endpoints: List[AnyEndpoint]
  )(implicit runtime: IORuntime)
    extends ServerBuilder[MainTask] {


    val customServerOptions = FinatraServerOptions.customiseInterceptors.decodeFailureHandler(
      ctx => {
        println("decode failure")
        DefaultDecodeFailureHandler.default(ctx)
      }
    ).exceptionHandler(ExceptionHandler[TFuture] { ctx =>
      println("exeption handler")
      TFuture.value(Some(ValuedEndpointOutput[String](stringBody, s"Internal Server Error, exception")))
    })
      .rejectHandler(RejectHandler[TFuture] { f =>
        println("reject handler")
        TFuture.value(Some(ValuedEndpointOutput[String](stringBody, s"Internal Server Error, exception")))
      }).options

    override def addRoute[I, E, O](
        endpoint: Endpoint[Unit, I, E, O, Any],
        logic: I => MainTask[O],
        errorTr: Throwable => MainTask[E]
    ): ServerBuilder[MainTask] =
      new ServerBuilderImpl(
        routes :+ FinatraServerInterpreter(customServerOptions).toRoute(
          endpoint
            .serverLogic(
              i =>
                logic(i)
                  .map(_.asRight[E])
                  .handleErrorWith(errorTr(_).map(_.asLeft[O]))
                  .run(Context(TrackingIdGenerator.generate))
                  .asTwitterFuture
            )
        ),
        endpoints :+ endpoint
      )

    override def getEndpoints: List[AnyEndpoint] = endpoints

    override def server(port: Int): HttpServer = {
      val yaml: String =
        OpenAPIDocsInterpreter()
          .toOpenAPI(endpoints, "ActivityHub", "1.0")
          .toYaml
      val swagger = SwaggerUI[TFuture](yaml)
      val sw      = swagger.map(FinatraServerInterpreter().toRoute(_))

      mkServer(routes ::: sw, port)
    }
  }

  private class FinatraController(routers: List[FinatraRoute])
    extends Controller with TapirController {
    routers.foreach(addTapirRoute)
  }

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

  private def mkServer(s: List[FinatraRoute], port: Int) = new HttpServer {
    override def defaultHttpPort: String = s":$port"

    protected override def disableAdminHttpServer: Boolean = true

    protected override def configureHttp(router: HttpRouter): Unit =
      router.add(cors, new FinatraController(s))
  }
}
