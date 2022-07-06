package ru.activity.hub.api

import cats.syntax.all._
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.http.{Controller, HttpServer}
import com.twitter.util.{Duration, Future => TFuture}
import io.estatico.newtype.macros.newtype
import ru.activity.hub.api.domain.{Pet, Response}
import sttp.tapir.docs.openapi._
import sttp.tapir.generic.auto._
import sttp.tapir.json.tethysjson._
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.server.finatra.{FinatraRoute, FinatraServerInterpreter, FinatraServerOptions, TapirController}
import sttp.tapir.server.{DecodeFailureHandling, DefaultDecodeFailureResponse, ServerDefaults, ServerEndpoint}
import sttp.tapir.swagger.finatra.SwaggerFinatra
import sttp.tapir.ztapir._
import tethys.derivation.semiauto.{jsonReader, jsonWriter}
import tethys.{JsonObjectWriter, JsonReader, JsonWriter}
import zio.interop.twitter._
import zio.{App, ExitCode, RIO, Task, UIO, URIO, _}

import scala.util.matching.Regex

object ZioExampleFinatraServer extends App {

  implicit val runtime: Runtime[zio.ZEnv] = zio.Runtime.default
  // Sample endpoint, with the logic implemented directly using .toRoutes

  type F[A] = RIO[Any, A]
  def serverLogic(in: Int): Task[Pet] =
    if (in == 35) {
      UIO(println("good")) *> Task(Pet("Tapirus terrestris", "https://en.wikipedia.org/wiki/Tapir"))
    } else {
      UIO(println("good")) *> Task.fail(new Exception("asd"))
    }

  def endPointHandler[T](in: ZIO[Any, Throwable, T]): UIO[Response[T]] =
    in.either
      .catchAllDefect(th => UIO(Left(th)))
      .flatMap {
        case Left(value)  => UIO(println("good")) *> UIO(Response(None, "bad", Some(value.toString)))
        case Right(value) => UIO(println("good")) *> UIO(Response(Some(value), "good", None))
      }

  val petEndpoint: ServerEndpoint[Int, Unit, Response[Pet], Any, TFuture] =
    endpoint.get
      .in("pet" / path[Int]("petId"))
//      .errorOut(stringBody)
      .out(jsonBody[Response[Pet]])
      .serverLogic { petId =>
        {
          runtime.unsafeRunToTwitterFuture(endPointHandler(serverLogic(petId)).map(_.asRight[Unit]))
        }
      }

  val yaml: String = OpenAPIDocsInterpreter.serverEndpointsToOpenAPI[TFuture](Seq(petEndpoint), "Our pets", "1.0").toYaml

  val failureOutput = ServerDefaults.failureOutput(jsonBody[Response[Boolean]])


  def failureHandler(response: DefaultDecodeFailureResponse, message: String):DecodeFailureHandling =
    DecodeFailureHandling.response(failureOutput) {
      (response, Response(payload = None, resultCode = "Bad", message = Some(message)))
    }
  val myDecodeFailureHandler = ServerDefaults.decodeFailureHandler.copy(
    response = failureHandler,
    failureMessage = _ => "i dont know"
  )
  implicit val serveroptions: FinatraServerOptions =
    FinatraServerOptions.default.copy(
      decodeFailureHandler = ctx => {
        ctx.input match {
          case in => DecodeFailureHandling.response(anyJsonBody[Response[Boolean]]) {
            Response(payload = None, resultCode = "Bad", message = Some(ctx.failure.toString))
          }
        }
      }
//      logRequestHandling = LogRequestHandling(
//        doLogWhenHandled = ???, doLogAllDecodeFailures = ???, doLogLogicExceptions = ???, noLog = ???, logWhenHandled = ???, logAllDecodeFailures = ???, logLogicExceptions = ???)
    )

  // Starting the server
  val serve: FinatraRoute =
    FinatraServerInterpreter.toRoute(petEndpoint)(serveroptions)

  val swagger = new SwaggerFinatra(yaml)
  class FinatraController(routers: List[FinatraRoute]) extends Controller with TapirController {
    routers.foreach(addTapirRoute)
  }

  def server(s: List[FinatraRoute]) = new HttpServer {
    override def defaultHttpsPort: String = ":7777"

    protected override def disableAdminHttpServer: Boolean = true

    protected override def configureHttp(router: HttpRouter): Unit = {
      router.add(new FinatraController(s))
      router.add(swagger)
    }
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    Task.effect(server(List(serve)).main(Array.empty[String])).exitCode


}

object domain {
  case class Response[T](payload: Option[T], resultCode: String, message: Option[String])
  object Response {
    implicit def responseJsonWriter[T: JsonWriter]: JsonWriter[Response[T]] =
    {
      jsonWriter[Response[T]]
    }
    implicit def responseJsonReader[T: JsonReader]: JsonReader[Response[T]] =
      jsonReader[Response[T]]

  }

  case class Pet(species: String, url: String)
  object Pet {
    implicit def petJsonWriter: JsonWriter[Pet] = jsonWriter[Pet]
    implicit def petJsonReader: JsonReader[Pet] = jsonReader[Pet]

  }
}

object ttt {
case class A(s: String) extends AnyVal

  val reg: Regex = "\\w+".r
  val big = "SASKNCLKASCLA"
  big.split(Array(',', '.'))
  val a = A("as")

}