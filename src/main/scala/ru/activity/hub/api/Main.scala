package ru.activity.hub.api

import cats.data.ReaderT
import cats.effect.Resource.eval
import cats.effect.{ExitCode, IO, IOApp, Resource}
import ru.activity.hub.api.components.HttpComponent.Modules
import ru.activity.hub.api.components._
import ru.activity.hub.api.handlers.system.SystemModule
import ru.activity.hub.api.handlers.users.UserHandlers
import ru.activity.hub.api.infrastructure.Context
import ru.activity.hub.api.infrastructure.MainTask.{MainTask, _}
import ru.activity.hub.api.infrastructure.http.ResponseComplete
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.services.domain.User

class Main {
  val start: Resource[MainTask, Unit] =
    for {
      configs   <- eval(ConfigComponent.build[MainTask])
      executors <- ExecutionComponent.build[MainTask]
      implicit0(session: SessionManager[MainTask, User.Id]) <- SessionComponent
        .build[MainTask]
      rc = ResponseComplete.make
      db       <- DatabaseComponent.build[MainTask]
      services <- eval(ServicesComponent.build[MainTask](db))
      httpModules = Modules(
        new SystemModule[MainTask](session, rc) ::
        new UserHandlers[MainTask](services.userModule, rc) ::
        //          new ActivityHandlers[MainTask, HttpTask](services.activityModule) ::
        Nil
      )
      _ <- HttpComponent.build(httpModules, configs.config.httpConfig)(
        executors.runtime
      )
    } yield ()

}

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] =
    new Main().start
      .use(_ => ReaderT.liftF[IO, Context, Unit](IO.never))
      .run(Context("initial"))
      .map(a => ExitCode.Success)
      .handleError { th =>
        println(th.printStackTrace())
        ExitCode.Error
      }
}
