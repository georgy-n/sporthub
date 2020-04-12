package ru.activity.hub.api.services.activity

import ru.activity.hub.api.services.activity.ActivityService.ActivityOfferRequest
import ru.activity.hub.api.services.activity.domain.{Activity, Category, SubCategory}
import ru.activity.hub.api.services.domain.User

trait ActivityService[F[_]] {

  def getAllActivities: F[List[Activity]]

  def saveActivity(userId: User.Id, activity: Activity): F[Unit]

  def deleteActivity(userId: User.Id, activityId: Activity.Id): F[Unit]

  def getCategories: F[List[Category]]

  def addActivityOffer(req: ActivityOfferRequest, userId: User.Id): F[Activity]
}

object ActivityService {
  case class ActivityOfferRequest(
      category: Category.Name,
      subCategory: SubCategory.Name,
      description: String,
      countPerson: Int
  )
}
