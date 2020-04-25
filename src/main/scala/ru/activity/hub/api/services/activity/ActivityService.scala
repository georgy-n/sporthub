package ru.activity.hub.api.services.activity

import java.time.LocalDateTime

import ru.activity.hub.api.components.handlers.users.domain.Done
import ru.activity.hub.api.services.activity.ActivityService.{ActivityOfferRequest, Filters}
import ru.activity.hub.api.services.activity.domain.{Activity, ActivityInfo, Category, SubCategory}
import ru.activity.hub.api.services.domain.User

trait ActivityService[F[_]] {

  def getAllActivities: F[List[Activity]]

  def saveActivity(userId: User.Id, activity: Activity): F[Unit]

  def deleteActivity(userId: User.Id, activityId: Activity.Id): F[Unit]

  def getCategories: F[List[Category]]

  def addActivityOffer(req: ActivityOfferRequest, userId: User.Id): F[Activity]

  def search(filters: Filters): F[List[Activity]]

  def getActivityInfo(req: Activity.Id): F[ActivityInfo]

  def subscribe(userId: User.Id, activityId: Activity.Id): F[Done]

  def unSubscribe(userId: User.Id, activityId: Activity.Id): F[Done]
}

object ActivityService {
  case class ActivityOfferRequest(
      category: Category.Name,
      subCategory: SubCategory.Name,
      description: String,
      countPerson: Int,
      date: LocalDateTime
  )

  case class Filters(category: Option[String], subCategory: Option[String])
}
