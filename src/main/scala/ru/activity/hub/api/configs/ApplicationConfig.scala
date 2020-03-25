package ru.activity.hub.api.configs

case class ApplicationConfig(httpConfig: HttpConfig)
case class HttpConfig(port: Int)