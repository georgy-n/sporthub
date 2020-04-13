package ru.activity.hub.api.services.activity

import doobie.util.meta.Meta
import ru.activity.hub.api.services.activity.domain.ActivityStatus

package object repo {
  implicit val statusMeta: Meta[ActivityStatus] =
    Meta.StringMeta.imap(ActivityStatus.withName(_))(_.entryName)
}
