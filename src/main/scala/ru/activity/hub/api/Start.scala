package ru.activity.hub.api

import com.twitter.finagle.http.filter.Cors
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.util.Future
import sttp.apispec.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.tethysjson.jsonBody
import sttp.tapir.{PublicEndpoint, endpoint, plainBody, stringBody}
import sttp.tapir.server.finatra.{FinatraRoute, FinatraServerInterpreter, TapirController}
import sttp.tapir.swagger.SwaggerUI
import com.twitter.util.{Future => TFuture}

object Start extends App {

  def countCharacters: Future[Either[Unit, String]] =
    Future.value(Right[Unit, String]("RESULT"))

  val countCharactersEndpoint: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint.get.in("version").out(plainBody[String])

  val countCharactersRoute: FinatraRoute =
    FinatraServerInterpreter().toRoute(countCharactersEndpoint.serverLogic(_ => countCharacters))

  val yaml: String =
    OpenAPIDocsInterpreter()
      .toOpenAPI(countCharactersEndpoint, "ActivityHub", "1.0")
      .toYaml
  val swagger                                      = SwaggerUI[TFuture](yaml)

  val sw = swagger.map(FinatraServerInterpreter().toRoute(_))

  val serv = new MyServer(List(countCharactersRoute) ::: sw, 1337)
  serv.main(Array.empty[String])

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

  class MyServer(
      s: List[FinatraRoute],
      port: Int
  ) extends HttpServer {
    override def defaultHttpPort: String = s":$port"

    protected override def disableAdminHttpServer: Boolean = true

    protected override def configureHttp(router: HttpRouter): Unit = {
      router.add(cors, new FinatraController(s))//.add(cors)
    }
  }
}
