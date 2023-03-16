package ru.activity.hub.api

import cats.data.{ReaderT, StateT}
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.effect.Resource.eval
import cats.effect.unsafe.IORuntime
import ru.activity.hub.api.components.HttpComponent.Modules
import ru.activity.hub.api.components.{ConfigComponent, DatabaseComponent, ExecutionComponent, HttpComponent, ServicesComponent, SessionComponent}
import ru.activity.hub.api.handlers.system.SystemModule
import ru.activity.hub.api.infrastructure.Context
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.infrastructure.MainTask._
import ru.activity.hub.api.infrastructure.http.ResponseComplete

class Application {
  val start: Resource[MainTask, Unit] =
    for {
      configs <- eval(ConfigComponent.build[MainTask])
      executors <- ExecutionComponent.build[MainTask]
      implicit0(session: SessionManager[MainTask, User.Id]) <- SessionComponent.build[MainTask]
      rc = ResponseComplete.make
//      db <- DatabaseComponent.build[MainTask]
//      services <- liftF(ServicesComponent.build[MainTask](db))
      httpModules = Modules(
        new SystemModule[MainTask](session, rc) ::
          //          new UserHandlers[MainTask, HttpTask](services.userModule) ::
          //          new ActivityHandlers[MainTask, HttpTask](services.activityModule) ::
          Nil
      )
      _ <- HttpComponent.build(httpModules, configs.config.httpConfig)(executors.runtime)
    } yield ()

}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    new Application().start
    .use(_ => ReaderT.liftF[IO, Context, Unit](IO.never)).run(Context("initial"))
    .map(a => ExitCode.Success)
    .handleError{ th =>
      println(th.printStackTrace())
      ExitCode.Error }
}
