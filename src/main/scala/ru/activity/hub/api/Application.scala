package ru.activity.hub.api

import cats.effect.Resource
import cats.effect.Resource.liftF
import ru.activity.hub.api.components.HttpComponent.Modules
import ru.activity.hub.api.components.handlers.system.SystemModule
import ru.activity.hub.api.components.{ConfigComponent, DatabaseComponent, ExecutionComponent, HttpComponent, ServicesComponent, SessionComponent}
import ru.activity.hub.api.infrastructure.Context
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.services.domain.User
import zio.ZIO
import zio.interop.catz._
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.infrastructure.MainTask._
import zio.internal.Platform

class Application {
  val start: Resource[MainTask, Unit] =
    for {
      configs <- liftF(ConfigComponent.build[MainTask])
      executors <- ExecutionComponent.build[MainTask]
      implicit0(session: SessionManager[MainTask, User.Id]) <- SessionComponent.build[MainTask]
//      db <- DatabaseComponent.build[MainTask]
//      services <- liftF(ServicesComponent.build[MainTask](db))
      _ <- HttpComponent.build(
        Modules(
          new SystemModule(executors.http) ::
//          new UserHandlers[MainTask, HttpTask](services.userModule) ::
//          new ActivityHandlers[MainTask, HttpTask](services.activityModule) ::
            Nil
        ))(
        configs.config.httpConfig,
        executors.http
      )
    } yield ()

}

object Main extends App {

    zio.Runtime.default.unsafeRun(new Application().start
      .use(_ => ZIO.never)
      .catchAllCause(th => ZIO.effect(println(th)))
      .provide(Context("initial")
      )
  )
}
