package ru.activity.hub.api


import ru.activity.hub.api.infrastructure.executors.ExecutorCreator
import zio.DefaultRuntime
import zio.internal.{Executor, Platform, PlatformLive}

object DRuntime extends DefaultRuntime {}

object CustomRuntime {
  def platform(prefix: String, k: Int): Platform = {
    val procCount = Runtime.getRuntime.availableProcessors()

    PlatformLive.Default
      .withExecutor(Executor.fromThreadPoolExecutor(_ =>
        PlatformLive.defaultYieldOpCount)(
        ExecutorCreator.newThreadPool(procCount * k,procCount * k, 0, prefix )
      ))
  }
}
