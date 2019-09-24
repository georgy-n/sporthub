//import tofu.
import java.util.concurrent.atomic.AtomicReference

import tofu.memo

object Application extends App {

  class A(val a: String)
  case class Config(v1: Int, v2: Int)
  case class State(config: Config)

  val state = State(Config(1, 2))
  val ref = new AtomicReference[State](state)


  val newConfig = State(Config(1, 2))

  println(ref.compareAndSet(newConfig, state))

}
