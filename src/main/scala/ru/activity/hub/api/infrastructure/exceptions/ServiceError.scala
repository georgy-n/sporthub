package ru.activity.hub.api.infrastructure.exceptions

class BaseError(msg: String, cause: Throwable = null) extends Exception(msg, cause)

case class ServiceError(msg: String) extends BaseError(msg)