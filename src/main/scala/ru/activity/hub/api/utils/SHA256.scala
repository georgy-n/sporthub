package ru.activity.hub.api.utils

import java.math.BigInteger
import java.security.MessageDigest

object SHA256 {
  def encode(string: String): String =
    String.format(
      "%032x",
      new BigInteger(
        1,
        MessageDigest
          .getInstance("SHA-256")
          .digest("some string".getBytes("UTF-8"))
      )
    )
}
