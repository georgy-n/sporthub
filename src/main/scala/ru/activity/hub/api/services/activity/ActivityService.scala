package ru.activity.hub.api.services.activity

import java.time.LocalDateTime

import ru.activity.hub.api.components.handlers.users.domain.Done
import ru.activity.hub.api.services.activity.ActivityService.{ActivityOfferRequest, Filters, SetCommentRequest}
import ru.activity.hub.api.services.activity.domain.{Activity, ActivityInfo, Category, Comment, SubCategory}
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

  def getSubscribedActivity(userId: User.Id): F[List[Activity]]

  def setComment(userId: User.Id, req: SetCommentRequest): F[Comment]

  def getComments(activityId: Activity.Id): F[List[Comment]]
}

object ActivityService {
  case class ActivityOfferRequest(
      category: Category.Name,
      subCategory: SubCategory.Name,
      description: String,
      countPerson: Int,
      date: LocalDateTime
  )

  case class SetCommentRequest(activityId: Activity.Id, date: LocalDateTime, message: String)
  case class Filters(category: Option[String], subCategory: Option[String])
}
