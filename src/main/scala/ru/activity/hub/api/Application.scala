package ru.activity.hub.api

import buildinfo.BuildInfo
import cats.effect.Resource
import ru.activity.hub.api.components.HttpComponent
import ru.activity.hub.api.components.HttpComponent.Modules
import ru.activity.hub.api.components.handlers.system.SystemModule
import ru.activity.hub.api.infrastructure.HttpTask.HttpTask
import ru.activity.hub.api.infrastructure.MainTask.MainTask
import ru.activity.hub.api.infrastructure.HttpTask._
import zio.interop.catz._
import cats.syntax.all._
import ru.activity.hub.api.infrastructure.Context
import zio.{DefaultRuntime, ZIO}

class Application {
//
val defRuntime = new DefaultRuntime {}

  val start: Resource[MainTask, Unit]  =
//
    for {
//      configs <- ConfigComponent.build[MainTask]()
//      db <- DBComponent.build[MainTask]()
      httpComp <- HttpComponent.build(Modules(new SystemModule[MainTask, HttpTask] :: Nil))(defRuntime)
    } yield ()

}

object Main extends App {
  val defRuntime = new DefaultRuntime {}

  defRuntime.unsafeRun(
    new Application().start
      .use(_ => ZIO.never)
//      .catchAllCause(th => )
      .provide(Context("initial", None))
  )
}