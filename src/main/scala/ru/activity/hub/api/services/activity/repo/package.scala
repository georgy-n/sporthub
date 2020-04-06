package ru.activity.hub.api.services.activity

import doobie.util.Meta
import ru.activity.hub.api.services.activity.domain.{ActivityStatus, Category, SubCategory}

package object repo {
  implicit val categoryMeta: Meta[Category] =
    Meta.StringMeta.imap(Category(_))(_.name)


  implicit val subCategoryMeta: Meta[SubCategory] =
    Meta.StringMeta.imap(SubCategory(_))(_.name)


  implicit val statuMeta: Meta[ActivityStatus] =
    Meta.StringMeta.imap(ActivityStatus.withName(_))(_.entryName)
}
