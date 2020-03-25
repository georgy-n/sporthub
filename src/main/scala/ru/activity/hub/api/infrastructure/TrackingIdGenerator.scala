package ru.activity.hub.api.infrastructure

import scala.util.Random

object TrackingIdGenerator {
  private val symbols = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890"
  def generate: String = {
    val size = symbols.size
    (1 to 7).map(_ => symbols(Random.nextInt.abs % size)).mkString
  }
}
