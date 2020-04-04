package ru.activity.hub.api.infrastructure.session

import cats.effect.Sync

trait SessionGenerator[F[_]] {
  def generate(): F[String]
}

// TODO improve me
class SessionGeneratorImpl[F[_]: Sync] extends SessionGenerator[F] {
  override def generate(): F[String] = Sync[F].delay(java.util.UUID.randomUUID().toString)
}