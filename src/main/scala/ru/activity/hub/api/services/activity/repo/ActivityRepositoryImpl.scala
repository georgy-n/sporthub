package ru.activity.hub.api.services.activity.repo

import cats.effect.Bracket
import cats.syntax.all._
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import ru.activity.hub.api.services.activity.domain.{Activity, Category, SubCategory}
import ru.activity.hub.api.infrastructure.DoobieInstances._
import ru.activity.hub.api.services.activity.repo.ActivityRepository.ActivityOffer
import ru.activity.hub.api.services.activity.repo.ActivityRepositoryImpl.CategoryRaw

class ActivityRepositoryImpl[F[_]](transactor: Transactor[F])(implicit bracket: Bracket[F, Throwable])
  extends ActivityRepository[F] {
  def getAllActivities: F[List[Activity]] =
    sql"""select
            activity_id,
            activity_category,
            activity_subcategory,
            activity_description,
            activity_owner,
            activity_count_person,
            activity_status from activity"""
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
              activity_count_person
            )
           VALUES (${offer.category}, ${offer.subCategory}, ${offer.description}, ${offer.ownerId}, ${offer.countPerson})
         """.update
      .withUniqueGeneratedKeys[Activity](
        "activity_id",
        "activity_category",
        "activity_subcategory",
        "activity_description",
        "activity_owner",
        "activity_count_person",
        "activity_status"
      )
      .transact(transactor)

}

object ActivityRepositoryImpl {
  case class CategoryRaw(name: Category.Name, subCategory: SubCategory.Name)
}
