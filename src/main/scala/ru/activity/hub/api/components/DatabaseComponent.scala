package ru.activity.hub.api.components

import cats.effect.{Async, Blocker, ContextShift, Resource}
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor
import ru.activity.hub.api.infrastructure.executors.ExecutorCreator

case class DatabaseComponent[F[_]](db: Transactor[F])

object DatabaseComponent {
  def build[F[_]: Async: ContextShift]: Resource[F, DatabaseComponent[F]] = {
    def newTransactor: Resource[F, HikariTransactor[F]] = {
      val config = new HikariConfig()
      config.setJdbcUrl("")
      config.setUsername("")
      config.setPassword("")
      config.setMaximumPoolSize(5)

      for {
        ce <- ExecutorCreator.fixedExecutionContextResource(2, "doobie-ec") // our connect EC
        be <- ExecutorCreator.executionContextResource(5, 4 * 5, 60000L, "doobie-blocker-")
          .map(Blocker.liftExecutionContext) // our blocking EC
        xa <- HikariTransactor.fromHikariConfig[F](
          config,
          ce, // await connection here
          be // execute JDBC operations here
        )
      } yield xa
    }

    for {
      mainDb <- newTransactor
    } yield DatabaseComponent(mainDb)
  }
}
