//package ru.activity.hub.api.components.handlers.activity
//
//import cats.Monad
//import cats.effect.Sync
//import ru.activity.hub.api.components.handlers.users.domain.Done
//import ru.activity.hub.api.infrastructure.http.{Entry, HttpModule, ReqCompleter, _}
//import ru.activity.hub.api.services.domain.User
//import ru.activity.hub.api.infrastructure.NewTypeInstances._
//import ru.activity.hub.api.infrastructure.session.SessionManager
//import ru.activity.hub.api.services.activity.ActivityModule
//import ru.activity.hub.api.services.activity.ActivityService.{ActivityOfferRequest, EditActivityRequest, Filters, SetCommentRequest}
//import ru.activity.hub.api.services.activity.domain.{Activity, ActivityInfo, Category, Comment}
//import ru.tinkoff.tschema.finagle._
//import ru.tinkoff.tschema.syntax._
//import ru.tinkoff.tschema.finagle.tethysInstances._
//
//final class ActivityHandlers[F[_]: Sync, HttpF[_]: Monad: RoutedPlus: LiftHttp[*[_], F]: ReqCompleter](
//    activityModule: ActivityModule[F]
//)(implicit sm: SessionManager[F, User.Id])
//  extends HttpModule[HttpF] {
//  import ru.activity.hub.api.components.handlers.Auth.userAuth2
//  import ru.activity.hub.api.components.handlers.activity.domain._
//  import ru.activity.hub.api.components.handlers.tethysRW._
//
//  override val entry = Entry(MkService[HttpF](api)(handler))
//
//  def api =
//    prefix('activity) |> (
//      (
//        get |>
//        operation('getAll) |>
//        $$$[List[Activity]]
//      )
//      <|> (
//        get |>
//        operation('getCategories) |>
//        $$$[List[Category]]
//      ) <|> (
//        post |>
//        operation('addActivityOffer) |>
//        bearerAuth[User.Id]('users, 'userId) |>
//        reqBody[ActivityOfferRequest] |>
//        $$$[Activity]
//      ) <|> (
//        get |>
//        operation('search) |>
//        queryParam[Filters]("filters") |>
//        $$$[List[Activity]]
//      ) <|> (
//        get |>
//        operation('activityInfo) |>
//        queryParam[Activity.Id]("activityId") |>
//        $$$[ActivityInfo]
//      ) <|> (
//        post |>
//        operation('subscribe) |>
//        bearerAuth[User.Id]('users, 'userId) |>
//        queryParam[Activity.Id]("activityId") |>
//        $$$[Done]
//      ) <|> (
//        post |>
//          operation('unSubscribe) |>
//          bearerAuth[User.Id]('users, 'userId) |>
//          queryParam[Activity.Id]("activityId") |>
//          $$$[Done]
//        )<|> (
//        get |>
//          operation('subscribed) |>
//          bearerAuth[User.Id]('users, 'userId) |>
//          $$$[List[Activity]]
//        )<|> (
//        get |>
//          operation('comments) |>
//          queryParam[Activity.Id]("activityId") |>
//          $$$[List[Comment]]
//        )<|> (
//        post |>
//          operation('comment) |>
//          bearerAuth[User.Id]('users, 'userId) |>
//          reqBody[SetCommentRequest] |>
//          $$$[Comment]
//        ) <|> (
//        post |>
//          operation('edit) |>
//          bearerAuth[User.Id]('users, 'userId) |>
//          reqBody[EditActivityRequest] |>
//          $$$[Done]
//        ) <|> (
//        post |>
//          operation('delete) |>
//          bearerAuth[User.Id]('users, 'userId) |>
//          reqBody[DeleteActivityRequest] |>
//          $$$[Done]
//        )
//    )
//  object handler {
//    def getAll(): F[List[Activity]]      = activityModule.activityService.getAllActivities
//
//    def getCategories: F[List[Category]] = activityModule.activityService.getCategories
//
//    def addActivityOffer(userId: User.Id, body: ActivityOfferRequest): F[Activity] =
//      activityModule.activityService.addActivityOffer(body, userId)
//
//    def search(filters: Filters): F[List[Activity]] = activityModule.activityService.search(filters)
//
//    def activityInfo(activityId: Activity.Id): F[ActivityInfo] =
//      activityModule.activityService.getActivityInfo(activityId)
//
//    def subscribe(userId: User.Id, activityId: Activity.Id): F[Done] =
//      activityModule.activityService.subscribe(userId, activityId)
//
//    def unSubscribe(userId: User.Id, activityId: Activity.Id): F[Done] =
//      activityModule.activityService.unSubscribe(userId, activityId)
//
//    def subscribed(userId: User.Id): F[List[Activity]] =
//      activityModule.activityService.getSubscribedActivity(userId)
//
//    def comments(activityId: Activity.Id): F[List[Comment]] =
//      activityModule.activityService.getComments(activityId)
//
//    def comment(userId: User.Id, body: SetCommentRequest): F[Comment] =
//      activityModule.activityService.setComment(userId, body)
//
//    def edit(userId: User.Id, body: EditActivityRequest): F[Done] =
//      activityModule.activityService.editActivity(body)
//
//    def delete(userId: User.Id, body: DeleteActivityRequest): F[Done] = {
//      activityModule.activityService.deleteActivity(userId, body.id)
//    }
//  }
//}
