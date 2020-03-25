package ru.activity.hub.api.components

import cats.syntax.all._
import cats.effect.Sync
import com.typesafe.config.{Config, ConfigFactory}
import ru.activity.hub.api.configs.{ApplicationConfig, HttpConfig}

case class ConfigComponent(config: ApplicationConfig)

object ConfigComponent {
  def build[F[_]: Sync]: F[ConfigComponent] = {
    for {
      config <- Sync[F].delay(ConfigFactory.load())
      httpConfig = readHttpConfig(config)
    } yield ConfigComponent(ApplicationConfig(httpConfig))
  }

  private def readHttpConfig(config: Config): HttpConfig = {
    val port = config.getInt("port")
    HttpConfig(port)
  }
}
