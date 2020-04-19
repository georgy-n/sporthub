package ru.activity.hub.api.services.activity.repo

import java.time.LocalDateTime

import ru.activity.hub.api.services.activity.ActivityService.Filters
import ru.activity.hub.api.services.activity.domain.{Activity, Category, SubCategory}
import ru.activity.hub.api.services.activity.repo.ActivityRepository.ActivityOffer
import ru.activity.hub.api.services.domain.User

trait ActivityRepository[F[_]] {
  def getAllActivities: F[List[Activity]]
  def getCategories: F[List[Category]]
  def saveActivityOffer(offer: ActivityOffer): F[Activity]
  def findByFilters(filters: Filters): F[List[Activity]]
}

object ActivityRepository {
  case class ActivityOffer(
      ownerId: User.Id,
      category: Category.Name,
      subCategory: SubCategory.Name,
      description: String,
      countPerson: Int,
      date: LocalDateTime
  )
}
