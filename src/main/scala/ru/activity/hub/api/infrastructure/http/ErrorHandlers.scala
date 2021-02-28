package ru.activity.hub.api.infrastructure.http

import cats.instances.string._
import cats.syntax.all._
//import com.twitter.finagle.http.{Response, Status}
import ru.activity.hub.api.infrastructure.http.domain._
import tethys._
import tethys.jackson._
import zio._

object ErrorHandlers {
  // user related errors
  val CorsError                = "Неправильный CORS"
  val LimitError               = "Превышен лимит"
  val UnauthorizedError        = "Пользователь не авторизован"
  val MissingQueryParamError   = "Отсутствует обязательный параметр в запросе"
  val MalformedQueryParamError = "Неподдерживаемый формат параметра в запросе"
  val SwaggerAccessError       = "Доступ к Swagger запрещен"
  val NotFoundError            = "Запрашиваемый ресурс не найден"
  val UnhandledError           = "Необработанная ошибка запроса"
  val TimeoutError             = "Превышено время ответа"

  // internal error
  val SystemError = "Сервис временно недоступен. Попробуйте позже."

  // you can handle kinds of errors here
//  def handle(err: Throwable): HttpTask[Response] = err match {
//    case Rejected(rej) => errorResponseOf(rejectionMessage(rej), rej.status.code.toString, None, Status.NotFound)
//    case _             => errorResponseOf(SystemError, "500", None, Status.InternalServerError)
//  }
//
//  private def errorResponseOf(msg: String, code: String, info: Option[Map[String, String]], status: Status): HttpTask[Response] =
//    for {
//      trackingId  <- ZIO.access[Context](_.trackingId)
//      _ <- HttpTask.logging.error(msg)
//    } yield message.jsonResponse(
//      ResponseObj.error(ErrorPayload(msg, code, info), trackingId).asJson, status
//    )

//  private def rejectionMessage(rej: Rejection): String = {
//    if (rej.missing.nonEmpty)
//      MissingQueryParamError + rej.missing.map(_.name).mkString(" ", ",", "")
//    else if (rej.malformed.nonEmpty)
//      MalformedQueryParamError + rej.malformed.map(p => s"${p.name}: ${p.error}").mkString(" ", ",", "")
//    else if (rej.status == Status.Unauthorized)
//      UnauthorizedError
//    else if (rej.path =!= "" || rej.wrongMethod.nonEmpty)
//      NotFoundError
//    else UnhandledError
//  }
}
