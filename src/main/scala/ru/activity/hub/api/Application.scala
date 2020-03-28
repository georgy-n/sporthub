package ru.activity.hub.api

import buildinfo.BuildInfo
import cats.effect.Resource
import ru.activity.hub.api.components.{
  ConfigComponent,
  DatabaseComponent,
  ExecutionComponent,
  HttpComponent
}
import ru.activity.hub.api.components.HttpComponent.Modules
import ru.activity.hub.api.components.handlers.system.SystemModule
import ru.activity.hub.api.infrastructure.HttpTask.HttpTask
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.infrastructure.HttpTask._
import zio.interop.catz._
import cats.syntax.all._
import ru.activity.hub.api.infrastructure.Context
import zio.{DefaultRuntime, ZIO}
import cats.effect.Resource.liftF

class Application {
  val start: Resource[MainTask, Unit] =
    for {
      configs <- liftF(ConfigComponent.build[MainTask])
      executors <- ExecutionComponent.build[MainTask]
//      db <- DatabaseComponent.build[MainTask]
      httpComp <- HttpComponent.build(
        Modules(new SystemModule[MainTask, HttpTask] :: Nil))(
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
