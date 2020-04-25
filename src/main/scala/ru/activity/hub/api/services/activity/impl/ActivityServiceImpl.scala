package ru.activity.hub.api.services.activity.impl

import cats.MonadError
import cats.effect.Sync
import io.scalaland.chimney.dsl._

import ru.activity.hub.api.infrastructure.logging.Logging
import ru.activity.hub.api.services.activity.ActivityService
import ru.activity.hub.api.services.activity.ActivityService.{ActivityOfferRequest, Filters}
import ru.activity.hub.api.services.activity.domain.{Activity, ActivityInfo, Category}
import ru.activity.hub.api.services.activity.repo.ActivityRepository
import ru.activity.hub.api.services.activity.repo.ActivityRepository.ActivityOffer
import ru.activity.hub.api.services.domain.User
import cats.syntax.all._
import ru.activity.hub.api.infrastructure.exceptions.ServiceError

class ActivityServiceImpl[F[_]: Sync](repo: ActivityRepository[F])(
    implicit log: Logging[F],
    me: MonadError[F, Throwable]
) extends ActivityService[F] {
  def getAllActivities: F[List[Activity]] = repo.getAllActivities

  def saveActivity(userId: User.Id, activity: Activity): F[Unit] = ???

  def deleteActivity(userId: User.Id, activityId: Activity.Id): F[Unit] = ???

  def getCategories: F[List[Category]] = repo.getCategories

  def addActivityOffer(req: ActivityOfferRequest, userId: User.Id): F[Activity] =
    for {
      _   <- log.info(s"prepare to save $req")
      act <- repo.saveActivityOffer(req.into[ActivityOffer].withFieldConst(_.ownerId, userId).transform)
    } yield act

  def search(filters: Filters): F[List[Activity]] =
    repo.findByFilters(filters)

  def getActivityInfo(req: Activity.Id): F[ActivityInfo] =
    for {
      _ <- log.info("getInfo start")

      maybeActivity <- repo.findById(req)
      _ <- log.info(maybeActivity.toString)
      activity      <- me.fromOption(maybeActivity, ServiceError("activity not found"))
      participants  <- repo.findActivityParticipant(req)
      _ <- log.info(participants.toString)

      activityInfo = activity
        .into[ActivityInfo]
        .withFieldConst(_.participants, participants)
        .withFieldConst(_.countFreeSpace, activity.countPerson - participants.length)
        .transform
    } yield activityInfo
}
