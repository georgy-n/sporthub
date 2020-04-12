package ru.activity.hub.api.services.activity

import ru.activity.hub.api.services.activity.domain.{Activity, Category}
import ru.activity.hub.api.services.domain.User

trait ActivityService[F[_]] {

  def getAllActivities: F[List[Activity]]

  def saveActivity(userId: User.Id, activity: Activity): F[Unit]

  def deleteActivity(userId: User.Id, activityId: Activity.Id): F[Unit]

  def getCategories: F[List[Category]]
}
