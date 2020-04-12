package ru.activity.hub.api.services.activity.repo

import ru.activity.hub.api.services.activity.domain.{Activity, Category, SubCategory}

trait ActivityRepository[F[_]] {
  def getAllActivities: F[List[Activity]]
  def getCategories: F[List[Category]]
}
