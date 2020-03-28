package ru.activity.hub.api.infrastructure.executors

import java.util.concurrent.{ExecutorService, Executors, LinkedBlockingQueue, ThreadFactory, ThreadPoolExecutor, TimeUnit}

import cats.effect.{Resource, Sync}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

object ExecutorCreator {
  def fixedExecutionContextResource[F[_]: Sync](
                                                 size: Int,
                                                 prefix: String): Resource[F, ExecutionContextExecutor] = {
    val execService = Sync[F].delay(newFixedThreadPool(size, prefix))
    Resource
      .make(execService)(es => Sync[F].delay(es.shutdown()))
      .map(ExecutionContext.fromExecutor)
  }

  def executionContextResource[F[_]: Sync](
                                            size: Int,
                                            maxSize: Int,
                                            ttl: Long,
                                            prefix: String): Resource[F, ExecutionContext] = {
    val execService = Sync[F].delay(newThreadPool(size, maxSize, ttl, prefix))
    Resource
      .make(execService)(es => Sync[F].delay(es.shutdown()))
      .map(ExecutionContext.fromExecutor)
  }

  def newThreadPool(size: Int,
                            maxSize: Int,
                            ttl: Long,
                            prefix: String): ThreadPoolExecutor = {
    new ThreadPoolExecutor(
      size,
      maxSize,
      ttl,
      TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue[Runnable],
      factory(prefix)
    )
  }
  private def newFixedThreadPool(size: Int, prefix: String): ExecutorService = {
    new ThreadPoolExecutor(
      size,
      size,
      0l,
      TimeUnit.MILLISECONDS,
      new LinkedBlockingQueue[Runnable],
      factory(prefix)
    )
  }

  def factory(prefix: String): ThreadFactory = {
    val factory = Executors.defaultThreadFactory()

    r: Runnable =>
    {
      val thread = factory.newThread(r)
      thread.setName(prefix + thread.getName)
      thread
    }
  }

}
