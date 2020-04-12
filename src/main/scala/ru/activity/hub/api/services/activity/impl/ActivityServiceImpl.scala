package ru.activity.hub.api.services.activity.impl

import cats.effect.Sync
import io.scalaland.chimney.dsl._
import ru.activity.hub.api.infrastructure.logging.Logging
import ru.activity.hub.api.services.activity.ActivityService
import ru.activity.hub.api.services.activity.ActivityService.ActivityOfferRequest
import ru.activity.hub.api.services.activity.domain.{Activity, Category}
import ru.activity.hub.api.services.activity.repo.ActivityRepository
import ru.activity.hub.api.services.activity.repo.ActivityRepository.ActivityOffer
import ru.activity.hub.api.services.domain.User
import cats.syntax.all._

class ActivityServiceImpl[F[_]: Sync](repo: ActivityRepository[F])(implicit log: Logging[F]) extends ActivityService[F] {
  def getAllActivities: F[List[Activity]] = repo.getAllActivities

  def saveActivity(userId: User.Id, activity: Activity): F[Unit] = ???

  def deleteActivity(userId: User.Id, activityId: Activity.Id): F[Unit] = ???

  def getCategories: F[List[Category]] = repo.getCategories

  def addActivityOffer(req: ActivityOfferRequest, userId: User.Id): F[Activity] =
    for {
      _ <- log.info("prepare to save")
     act <-  repo.saveActivityOffer(req.into[ActivityOffer].withFieldConst(_.ownerId, userId).transform)
    } yield act
}
