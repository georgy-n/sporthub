package ru.activity.hub.api.components.handlers.activity

import cats.Monad
import cats.effect.Sync
import ru.activity.hub.api.infrastructure.http.{Entry, HttpModule, ReqCompleter, _}
import ru.activity.hub.api.services.domain.User
import ru.activity.hub.api.infrastructure.NewTypeInstances._
import ru.activity.hub.api.infrastructure.session.SessionManager
import ru.activity.hub.api.services.activity.ActivityModule
import ru.activity.hub.api.services.activity.ActivityService.ActivityOfferRequest
import ru.activity.hub.api.services.activity.domain.{Activity, Category}
import ru.tinkoff.tschema.finagle._
import ru.tinkoff.tschema.syntax._
import ru.tinkoff.tschema.finagle.tethysInstances._

final class ActivityHandlers[F[_]: Sync, HttpF[_]: Monad: RoutedPlus: LiftHttp[*[_], F]: ReqCompleter](
    activityModule: ActivityModule[F]
)(implicit sm: SessionManager[F, User.Id])
  extends HttpModule[HttpF] {
  import ru.activity.hub.api.components.handlers.Auth.userAuth2
  import ru.activity.hub.api.components.handlers.activity.domain._

  override val entry = Entry(MkService[HttpF](api)(handler))

  def api =
    prefix('activity) |> (
      (
        get |>
        operation('getAll) |>
        bearerAuth[User.Id]('users, 'userId) |>
        $$$[List[Activity]]
      )
      <|> (
        get |>
        operation('getCategories) |>
        $$$[List[Category]]
      ) <|> (
        post |>
        operation('addActivityOffer) |>
        bearerAuth[User.Id]('users, 'userId) |>
        reqBody[ActivityOfferRequest] |>
        $$$[Activity]
      )
    )
  object handler {
    def getAll(userId: User.Id): F[List[Activity]] = activityModule.activityService.getAllActivities
    def getCategories: F[List[Category]]           = activityModule.activityService.getCategories
    def addActivityOffer(userId: User.Id, body: ActivityOfferRequest): F[Activity] =
      activityModule.activityService.addActivityOffer(body, userId)

  }
}
