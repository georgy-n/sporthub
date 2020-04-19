package ru.activity.hub.api.infrastructure

import doobie.util.meta.Meta
import io.estatico.newtype.{BaseNewType, Coercible}
import ru.activity.hub.api.services.activity.domain.{Activity, Category, SubCategory}
import ru.activity.hub.api.services.domain.User
import ru.tinkoff.tschema.param.{MultiParam, ParamSource, SingleParam}
import tethys.writers.KeyWriter
import tethys.{JsonReader, JsonWriter}

object NewTypeInstances {
  implicit def NewTypeJsonWriter[R, N](
      implicit
//      LP: LowPriority,
      CC: Coercible[JsonWriter[R], JsonWriter[N]],
      R: JsonWriter[R]
  ): JsonWriter[N] = CC(R)

  implicit def NewTypeJsonReader[R, N](
      implicit
//      LP: LowPriority,
      CC: Coercible[JsonReader[R], JsonReader[N]],
      R: JsonReader[R]
  ): JsonReader[N] = CC(R)

  implicit def NewTypeKeyWriter[R, N](
      implicit
//      LP: LowPriority,
      CC: Coercible[KeyWriter[R], KeyWriter[N]],
      R: KeyWriter[R]
  ): KeyWriter[N] = CC(R)

  implicit def NewTypeParam[S >: ParamSource.All <: ParamSource, R, N](
      implicit
//      LP: LowPriority,
      CC: Coercible[SingleParam[S, R], SingleParam[S, N]],
      R: SingleParam[S, R]
  ): SingleParam[S, N] = CC(R)
}

trait LowPriorityInstances {
  implicit def newTypeDoobieMeta[B, T, R](implicit underlying: Meta[R]): Meta[BaseNewType.Aux[B, T, R]] =
    underlying.asInstanceOf[Meta[BaseNewType.Aux[B, T, R]]]
}

object DoobieInstances extends LowPriorityInstances {
  implicit val idMeta: Meta[User.Id] =
    Meta.StringMeta.imap(User.Id(_))(_.id)

  implicit val activityIdMeta: Meta[Activity.Id] =
    Meta.IntMeta.imap(Activity.Id(_))(_.id)

  implicit val categoryNameMeta: Meta[Category.Name] =
    Meta.StringMeta.imap(Category.Name(_))(_.value)

  implicit val subCategoryNameMeta: Meta[SubCategory.Name] =
    Meta.StringMeta.imap(SubCategory.Name(_))(_.value)
}
