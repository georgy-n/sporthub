package ru.activity.hub.api.services.activity.repo

import cats.effect.Bracket
import cats.syntax.all._
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import doobie.implicits.javatime._
import ru.activity.hub.api.services.activity.ActivityService.{EditActivityRequest, Filters}
import doobie.Fragments.whereAndOpt
import ru.activity.hub.api.services.activity.domain.{Activity, Category, Comment, SubCategory}
import ru.activity.hub.api.infrastructure.DoobieInstances._
import ru.activity.hub.api.services.activity.repo.ActivityRepository.{ActivityOffer, CommentRequest, Reservation}
import ru.activity.hub.api.services.activity.repo.ActivityRepositoryImpl.CategoryRaw
import ru.activity.hub.api.services.domain.User

class ActivityRepositoryImpl[F[_]](transactor: Transactor[F])(implicit bracket: Bracket[F, Throwable])
  extends ActivityRepository[F] {
  def getAllActivities: F[List[Activity]] =
    activitySelect
      .query[Activity]
      .to[List]
      .transact(transactor)

  def getCategories: F[List[Category]] =
    for {
      listCat <- sql"""select p_category_name, subcategory_name from subcategory"""
        .query[CategoryRaw]
        .to[List]
        .transact(transactor)
      result = listCat.groupBy(_.name).mapValues(_.map(_.subCategory)).toList.map(v => Category(v._1, v._2))
    } yield result

  def saveActivityOffer(offer: ActivityOffer): F[Activity] =
    sql""" INSERT INTO activity
           (
              activity_category,
              activity_subcategory,
              activity_description,
              activity_owner,
              activity_count_person,
              activity_date
            )
           VALUES (
              ${offer.category},
              ${offer.subCategory},
              ${offer.description},
              ${offer.ownerId},
              ${offer.countPerson},
              ${offer.date}
          )
         """.update
      .withUniqueGeneratedKeys[Activity](
        "activity_id",
        "activity_category",
        "activity_subcategory",
        "activity_description",
        "activity_owner",
        "activity_count_person",
        "activity_status",
        "activity_date"
      )
      .transact(transactor)

  def findByFilters(filters: Filters): F[List[Activity]] = {

    val byCategory    = filters.category.map(cat => fr"activity_category=$cat")
    val bySubCategory = filters.subCategory.map(cat => fr"activity_subcategory=$cat")
    val fragment      = activitySelect ++ whereAndOpt(byCategory) ++ whereAndOpt(bySubCategory)
    fragment
      .query[Activity]
      .to[List]
      .transact(transactor)
  }

  def findById(activityId: Activity.Id): F[Option[Activity]] = {
    val fragment = activitySelect ++ fr"""where activity_id=${activityId.id}"""

    fragment
      .query[Activity]
      .option
      .transact(transactor)
  }

  def findActivityParticipant(activityId: Activity.Id): F[List[User.Id]] =
    sql"""select
            reservation_user_id
            from reservation where reservation_activity_id=${activityId.id}"""
      .query[User.Id]
      .to[List]
      .transact(transactor)

  def subscribe(userId: User.Id, activityId: Activity.Id): F[Reservation] =
    sql""" INSERT INTO reservation
           (reservation_user_id, reservation_activity_id)
           VALUES (${userId.id}, ${activityId.id})
         """.update
      .withUniqueGeneratedKeys[Reservation]("reservation_id", "reservation_user_id", "reservation_activity_id")
      .transact(transactor)

  def unSubscribe(userId: User.Id, activityId: Activity.Id): F[Int] =
    sql"""DELETE
          from reservation
          where reservation_user_id=${userId.id} and reservation_activity_id=${activityId.id}
         """.update.run
      .transact(transactor)

  def getSubscribed(userId: User.Id): F[List[Activity.Id]] =
    sql"""select
            reservation_activity_id
            from reservation where reservation_user_id=${userId.id}"""
      .query[Activity.Id]
      .to[List]
      .transact(transactor)

  def saveComment(comment: CommentRequest): F[Comment] =
    sql""" INSERT INTO comments
           (
             comment_message,
             comment_date,
             comment_owner_id,
             comment_activity_id
            )
           VALUES (
              ${comment.message},
              ${comment.date},
              ${comment.commentOwner},
              ${comment.activityId}

          )
         """.update
      .withUniqueGeneratedKeys[Comment](
        "comment_id",
        "comment_activity_id",
        "comment_owner_id",
        "comment_date",
        "comment_message"
      )
      .transact(transactor)

  def findComments(activityId: Activity.Id): F[List[Comment]] = {
    val fragment = commentSelect ++ fr"""where comment_activity_id=${activityId.id}"""

    fragment
      .query[Comment]
      .to[List]
      .transact(transactor)
  }
  def deleteActivity(activityId: Activity.Id): F[Int] = {
    sql""" delete from activity
          where activity_id = ${activityId.id}
         """
      .update
      .run
      .transact(transactor)
  }

  def editActivity(edited: EditActivityRequest): F[Int] = {
    sql""" Update activity
            set
              activity_category = ${edited.category},
              activity_subcategory = ${edited.subCategory},
              activity_description = ${edited.description},
              activity_count_person = ${edited.countPerson},
              activity_date = ${edited.date}

          where activity_id = ${edited.id}
         """
      .update
      .run
      .transact(transactor)
  }

  private val commentSelect =
    fr"""
      select
        comment_id,
        comment_activity_id,
        comment_owner_id,
        comment_date,
        comment_message
            from comments
      """
  private val activitySelect = fr"""select
            activity_id,
            activity_category,
            activity_subcategory,
            activity_description,
            activity_owner,
            activity_count_person,
            activity_status,
            activity_date
            from activity"""
}

object ActivityRepositoryImpl {
  case class CategoryRaw(name: Category.Name, subCategory: SubCategory.Name)
}
