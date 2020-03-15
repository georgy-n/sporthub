package ru.activity.hub.infrastructure.logging

import ru.activity.hub.infrastructure.MainTask.MainTask

class MainTaskLogging extends Logging[MainTask] {
  override def info(message: String): MainTask[Unit] = ???

  override def warn(message: String): MainTask[Unit] = ???

  override def error(message: String): MainTask[Unit] = ???

  override def info(message: String, cause: Throwable): MainTask[Unit] = ???

  override def warn(message: String, cause: Throwable): MainTask[Unit] = ???

  override def error(message: String, cause: Throwable): MainTask[Unit] = ???
}
