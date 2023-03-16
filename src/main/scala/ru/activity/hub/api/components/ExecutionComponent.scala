package ru.activity.hub.api.components


import cats.effect.unsafe.IORuntime
import cats.effect.{Resource, Sync}
import ru.activity.hub.api.infrastructure.executors.ExecutorCreator


case class ExecutionComponent(runtime: IORuntime)


object ExecutionComponent {
  def build[F[_]: Sync]: Resource[F, ExecutionComponent] = {
    for {
      httpECE <- ExecutorCreator.fixedExecutionContextResource(4, "http-finagle-") // new fixed thread poll
//      main = CustomRuntime.platform("main-", 2) // MyRuntime.platfrom
//      http = Platform.default
//        .withExecutor(Executor.fromExecutionContext(1024)(httpECE))
      runtime = IORuntime.builder().build()

    } yield ExecutionComponent(runtime)
  }

}

