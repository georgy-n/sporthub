package ru.activity.hub.api.infrastructure.http

import cats.Monoid
import cats.syntax.semigroupk._
import com.twitter.finagle.http.Response
import ru.tinkoff.tschema.finagle.RoutedPlus

final case class Entry[F[_]](route: F[Response])
object Entry {
  implicit def monoid[F[_]](implicit routedPlus: RoutedPlus[F]): Monoid[Entry[F]] = new Monoid[Entry[F]] {
    val empty: Entry[F] = Entry(routedPlus.empty)
    def combine(x: Entry[F], y: Entry[F]): Entry[F] =
      Entry(x.route <+> y.route)
  }
}

