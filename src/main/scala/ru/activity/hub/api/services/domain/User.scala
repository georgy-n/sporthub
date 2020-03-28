package ru.activity.hub.api.services.domain

import io.estatico.newtype.macros.newtype


object User {
  @newtype case class Id(id: String)
}
case class User(userId: User.Id, firstName: String, secondName: String, login: String)
