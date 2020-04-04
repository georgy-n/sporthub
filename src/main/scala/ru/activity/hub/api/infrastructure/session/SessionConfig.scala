package ru.activity.hub.api.infrastructure.session

case class SessionConfig(
    serverSecret: String,
    sessionMaxAgeSeconds: Option[Long],
    removeUsedRefreshTokenAfter: Long
) {
  require(serverSecret.length >= 64,
          "Server secret must be at least 64 characters long!")
}
