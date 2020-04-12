package ru.activity.hub.api.infrastructure.session

import cats.effect.Sync
import cats.syntax.all._

import ru.activity.hub.api.infrastructure.logging.Logging

import scala.collection.mutable

trait SessionManager[F[_], T] {
  def get(session: String): F[Option[T]]
  def set(session: String, value: T): F[Unit]
  def remove(userId: T): F[Unit]
}

// TODO improve me
class SessionManagerImpl[F[_]: Sync, T](implicit log: Logging[F]) extends SessionManager[F, T] {
  private val _storage = mutable.Map.empty[String, T]

  override def get(session: String): F[Option[T]] =
    Sync[F].delay {
      _storage.get(session)
    } <* log.info(s"get $session")

  override def set(session: String, value: T): F[Unit] = Sync[F].delay {
    _storage.put(session, value)
  }

  override def remove(userId: T): F[Unit] = Sync[F].delay {
    _storage.find {
      case (_, u) => u == userId
    }.map {
      case (s, _) => _storage.remove(s)
    }
  }
}
