package ru.activity.hub.api.services.activity.repo

import ru.activity.hub.api.services.activity.domain.Activity

trait ActivityRepository[F[_]] {
  def getAllActivities(): F[List[Activity]]

}
