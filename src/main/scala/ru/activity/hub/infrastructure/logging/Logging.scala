package ru.activity.hub.infrastructure.logging

trait Logging[F[_]] {
  def info(message: String): F[Unit]
  def warn(message: String): F[Unit]
  def error(message: String): F[Unit]


  def info(message: String, cause: Throwable): F[Unit]
  def warn(message: String, cause: Throwable): F[Unit]
  def error(message: String, cause: Throwable): F[Unit]
}
