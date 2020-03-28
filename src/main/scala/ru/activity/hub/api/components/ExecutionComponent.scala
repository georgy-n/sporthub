package ru.activity.hub.api.components


import cats.effect.{Resource, Sync}
import ru.activity.hub.api.CustomRuntime
import ru.activity.hub.api.infrastructure.executors.ExecutorCreator
import zio.Runtime
import zio.internal.{Executor, PlatformLive}


case class ExecutionComponent(main: Runtime[Unit], http: Runtime[Unit])


object ExecutionComponent {
  def build[F[_]: Sync]: Resource[F, ExecutionComponent] = {
    for {
      httpECE <- ExecutorCreator.fixedExecutionContextResource(4, "http-finagle-") // new fixed thread poll
      main = CustomRuntime.platform("main-", 2) // MyRuntime.platfrom
      http = PlatformLive.Default
        .withExecutor(Executor.fromExecutionContext(1024)(httpECE))


    } yield ExecutionComponent(
      Runtime((), main),
      Runtime((),  http))
  }

}

