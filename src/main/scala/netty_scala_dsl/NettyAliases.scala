package netty_scala_dsl

/** A mixin to provide type aliases without the entire DSL. */
trait NettyAliases {
  type Message = Any
  type Failure = Throwable
}
