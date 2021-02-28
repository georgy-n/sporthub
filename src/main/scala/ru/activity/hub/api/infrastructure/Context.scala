package ru.activity.hub.api.infrastructure


final case class HttpContext(matched: Int,
                             path: CharSequence
//                             request: com.twitter.finagle.http.Request
                            )

final case class Context(trackingId: String,
                         httpContext: Option[HttpContext])
