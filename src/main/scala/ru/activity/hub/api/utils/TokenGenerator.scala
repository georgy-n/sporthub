package ru.activity.hub.api.utils

import cats.effect.Sync
import cats.syntax.all._

import ru.activity.hub.api.services.domain.User

object TokenGenerator {
  import java.security.MessageDigest
  import java.security.NoSuchAlgorithmException

  val MAGIC_KEY = "MAGIC_KEY"

  def createToken(user: User): String = {
    val expires = System.currentTimeMillis + 1000L * 60 * 60
    SHA256.encode(user.login) + ":" + expires + ":" + computeSignature(user.id, expires)
  }

  def computeSignature(userId: User.Id, expires: Long): String = SHA256.encode(s"$userId:$expires:$MAGIC_KEY")

  def getUserNameFromToken(authToken: String): String = {
    val parts = authToken.split(":")
    parts(0)
  }

  def validateToken[F[_]: Sync](authToken: String, userId: User.Id): F[Boolean] = for {
    parts <- Sync[F].delay(authToken.split(":"))
    expires <- Sync[F].delay(parts(1).toLong)
    signature <- Sync[F].delay(parts(2))
    signatureToMatch = computeSignature(userId, expires)
  } yield expires >= System.currentTimeMillis && signature == signatureToMatch
}
