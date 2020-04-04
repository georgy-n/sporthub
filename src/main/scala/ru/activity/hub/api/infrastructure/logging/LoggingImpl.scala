package ru.activity.hub.api.infrastructure.logging

import cats.effect.Sync
import org.slf4j.LoggerFactory

class LoggingImpl[F[_]: Sync](name: String) extends Logging[F] {
  private val logger = LoggerFactory.getLogger(name)

  override def info(message: String): F[Unit] = Sync[F].delay(logger.info(message))

  override def warn(message: String): F[Unit] = Sync[F].delay(logger.warn(message))

  override def error(message: String): F[Unit] = Sync[F].delay(logger.error(message))

  override def info(message: String, cause: Throwable): F[Unit] = Sync[F].delay(logger.info(message, cause))

  override def warn(message: String, cause: Throwable): F[Unit] = Sync[F].delay(logger.warn(message, cause))

  override def error(message: String, cause: Throwable): F[Unit] = Sync[F].delay(logger.error(message, cause))
}
