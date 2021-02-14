package ru.activity.hub.api


import ru.activity.hub.api.infrastructure.executors.ExecutorCreator
import zio.internal.{Executor, Platform}

object DRuntime {
  val default = Platform.default
}

object CustomRuntime {
  def platform(prefix: String, k: Int): Platform = {
    val procCount = Runtime.getRuntime.availableProcessors()

    Platform
      .default
      .withExecutor(Executor.fromThreadPoolExecutor(_ =>
        Platform.defaultYieldOpCount)(
        ExecutorCreator.newThreadPool(procCount * k,procCount * k, 0, prefix )
      ))
  }
}
