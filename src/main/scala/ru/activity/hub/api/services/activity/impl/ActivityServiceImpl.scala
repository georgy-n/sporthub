package ru.activity.hub.api.services.activity.impl

import ru.activity.hub.api.services.activity.ActivityService
import ru.activity.hub.api.services.activity.domain.Activity
import ru.activity.hub.api.services.activity.repo.ActivityRepository
import ru.activity.hub.api.services.domain.User

class ActivityServiceImpl[F[_]](repo: ActivityRepository[F]) extends ActivityService[F] {
  def getAllActivities(): F[List[Activity]] = repo.getAllActivities()

  def saveActivity(userId: User.Id, activity: Activity): F[Unit] = ???

  def deleteActivity(userId: User.Id, activityId: Activity.Id): F[Unit] = ???
}
