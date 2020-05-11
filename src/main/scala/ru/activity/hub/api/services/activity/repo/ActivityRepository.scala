package ru.activity.hub.api.services.activity.repo

import java.time.LocalDateTime

import ru.activity.hub.api.services.activity.ActivityService.Filters
import ru.activity.hub.api.services.activity.domain.{Activity, Category, Comment, SubCategory}
import ru.activity.hub.api.services.activity.repo.ActivityRepository.{ActivityOffer, CommentRequest, Reservation}
import ru.activity.hub.api.services.domain.User

trait ActivityRepository[F[_]] {
  def getAllActivities: F[List[Activity]]
  def getCategories: F[List[Category]]
  def saveActivityOffer(offer: ActivityOffer): F[Activity]
  def findByFilters(filters: Filters): F[List[Activity]]
  def findActivityParticipant(activityId: Activity.Id): F[List[User.Id]]
  def findById(activityId: Activity.Id): F[Option[Activity]]
  def subscribe(userId: User.Id, activityId: Activity.Id): F[Reservation]
  def unSubscribe(userId: User.Id, activityId: Activity.Id): F[Int]
  def getSubscribed(userId: User.Id): F[List[Activity.Id]]
  def findComments(activityId: Activity.Id): F[List[Comment]]
  def saveComment(comment: CommentRequest): F[Comment]
}

object ActivityRepository {
  case class Reservation(id: Int, userId: User.Id, activityId: Activity.Id)
  case class ActivityOffer(
      ownerId: User.Id,
      category: Category.Name,
      subCategory: SubCategory.Name,
      description: String,
      countPerson: Int,
      date: LocalDateTime
  )
  case class CommentRequest(activityId: Activity.Id, commentOwner: User.Id, date: LocalDateTime, message: String)

}
