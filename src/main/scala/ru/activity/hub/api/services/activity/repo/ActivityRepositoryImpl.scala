package ru.activity.hub.api.services.activity.repo

import cats.effect.Bracket
import doobie.util.transactor.Transactor
import doobie._
import doobie.implicits._
import ru.activity.hub.api.services.activity.domain.Activity
import ru.activity.hub.api.infrastructure.DoobieInstances._

class ActivityRepositoryImpl[F[_]](transactor: Transactor[F])(
    implicit bracket: Bracket[F, Throwable]) extends ActivityRepository[F] {
  def getAllActivities(): F[List[Activity]] = {
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
  }
}
