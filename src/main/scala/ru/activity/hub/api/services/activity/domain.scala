package ru.activity.hub.api.services.activity

import java.time.{Instant, LocalDateTime}

import io.estatico.newtype.macros.newtype
import enumeratum.{Enum, EnumEntry}
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.utils.Time
import tethys.enumeratum._

object domain {

  sealed trait ActivityStatus extends EnumEntry
  case object ActivityStatus extends Enum[ActivityStatus] with TethysEnum[ActivityStatus] {
    case object Open   extends ActivityStatus
    case object Closed extends ActivityStatus

    val values = findValues
  }

  object Category {
    @newtype case class Name(value: String)
  }
  object SubCategory {
    @newtype case class Name(value: String)
  }

  case class Category(name: Category.Name, subCategories: List[SubCategory.Name])

  case class Activity(
      id: Activity.Id,
      category: Category.Name,
      subCategory: SubCategory.Name,
      description: String,
      owner: User.Id,
      countPerson: Int,
      status: ActivityStatus,
      date: LocalDateTime
  )

  object Activity {
    @newtype case class Id(id: Int)
  }

  case class ActivityInfo(
      id: Activity.Id,
      category: Category.Name,
      subCategory: SubCategory.Name,
      description: String,
      owner: User.Id,
      countPerson: Int,
      status: ActivityStatus,
      date: LocalDateTime,
      countFreeSpace: Int,
      participants: List[User.Id]
  )
}
