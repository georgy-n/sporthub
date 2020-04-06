package ru.activity.hub.api.services.activity

import cats.effect.Sync
import cats.syntax.all._
import doobie.util.transactor.Transactor
import ru.activity.hub.api.infrastructure.logging.Logging
import ru.activity.hub.api.services.activity.impl.ActivityServiceImpl
import ru.activity.hub.api.services.activity.repo.ActivityRepositoryImpl

case class ActivityModule[F[_]](activityService: ActivityService[F])


object ActivityModule {
  def build[F[_]](transactor: Transactor[F])(
    implicit sync: Sync[F],
    logging: Logging[F]): F[ActivityModule[F]] = {
    for {
      _ <- logging.info("init Activity module")
      activityRepo = new ActivityRepositoryImpl[F](transactor)
      activityService = new ActivityServiceImpl[F](activityRepo)
      _ <- logging.info("inited Activity module")

    } yield ActivityModule(activityService)
  }
}