package ru.activity.hub.api

import cats.effect.Resource
import cats.effect.Resource.liftF
import ru.activity.hub.api.components.HttpComponent.Modules
import ru.activity.hub.api.components.handlers.activity.ActivityHandlers
import ru.activity.hub.api.components.handlers.system.SystemModule
import ru.activity.hub.api.components.handlers.users.UserHandlers
import ru.activity.hub.api.components.{ConfigComponent, DatabaseComponent, ExecutionComponent, HttpComponent, ServicesComponent, SessionComponent}
import ru.activity.hub.api.infrastructure.Context
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.services.domain.User
import zio.ZIO
import zio.interop.catz._
import ru.activity.hub.api.infrastructure.HttpTask.{HttpTask, _}
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import zio.internal.Platform

class Application {
  val start: Resource[MainTask, Unit] =
    for {
      configs <- liftF(ConfigComponent.build[MainTask])
      executors <- ExecutionComponent.build[MainTask]
      implicit0(session: SessionManager[MainTask, User.Id]) <- SessionComponent.build[MainTask]
      db <- DatabaseComponent.build[MainTask]
      services <- liftF(ServicesComponent.build[MainTask](db))
//      _ <- HttpComponent.build(
//        Modules(
//          new SystemModule[MainTask, HttpTask] ::
//          new UserHandlers[MainTask, HttpTask](services.userModule) ::
//          new ActivityHandlers[MainTask, HttpTask](services.activityModule) ::
//            Nil
//        )
//      )(
//        configs.config.httpConfig,
//        executors.main)
    } yield ()

}

object Main extends App {

    new Application().start
      .use(_ => ZIO.never)
//      .catchAllCause(th => )
      .provide(Context("initial", None)

  )
}
