package ru.activity.hub.api

import cats.effect.Resource
import cats.effect.Resource.liftF
import ru.activity.hub.api.components.HttpComponent.Modules
import ru.activity.hub.api.components.handlers.system.SystemModule
import ru.activity.hub.api.components.handlers.users.UserHandlers
import ru.activity.hub.api.components.{ConfigComponent, DatabaseComponent, ExecutionComponent, HttpComponent, ServicesComponent}
import ru.activity.hub.api.infrastructure.Context
import ru.activity.hub.api.infrastructure.HttpTask.{HttpTask, _}
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.services.user.UserModule
import zio.ZIO
import zio.interop.catz._

class Application {
  val start: Resource[MainTask, Unit] =
    for {
      configs <- liftF(ConfigComponent.build[MainTask])
      executors <- ExecutionComponent.build[MainTask]
      db <- DatabaseComponent.build[MainTask]
      services <- liftF(ServicesComponent.build[MainTask](db))
      httpComp <- HttpComponent.build(
        Modules(
          new SystemModule[MainTask, HttpTask] :: new UserHandlers[MainTask, HttpTask](services.userModule) :: Nil
        )
      )(
        configs.config.httpConfig,
        executors.main)
    } yield ()

}

object Main extends App {

  DRuntime.unsafeRun(
    new Application().start
      .use(_ => ZIO.never)
//      .catchAllCause(th => )
      .provide(Context("initial", None))
  )
}
