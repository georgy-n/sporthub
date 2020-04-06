package ru.activity.hub.api.services.activity

import io.estatico.newtype.macros.newtype
import enumeratum.{Enum, EnumEntry}
import ru.activity.hub.api.services.domain.User
import tethys.enumeratum._

object domain {

  sealed trait ActivityStatus extends EnumEntry
  case object ActivityStatus extends Enum[ActivityStatus] with TethysEnum[ActivityStatus] {
    case object Open extends ActivityStatus
    case object Closed extends ActivityStatus

    val values = findValues
  }

  case class Category(name: String)
  case class SubCategory(name: String)

  case class Activity(
      id: Activity.Id,
      category: Category,
      subCategory: SubCategory,
      description: String,
      owner: User.Id,
      countPerson: Int,
      status: ActivityStatus
  )

  object Activity {
    @newtype case class Id(id: Int)
  }
}
