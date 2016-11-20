package netty_scala_dsl

import java.net.{InetSocketAddress, SocketAddress}

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.epoll.{Epoll, EpollServerSocketChannel}
import io.netty.channel.socket.nio.NioServerSocketChannel

/** A mixin that provides flexible functions that form the networking DSL. */
trait NettyDSL extends NettyAliases {
  def newServerChannel(f: ServerBootstrap => Unit) = Epoll.isAvailable match {
    case true => newNativeServerChannel(f)
    case false => newNIOServerChannel(f)
  }

  def newNIOServerChannel(f: ServerBootstrap => Unit) = {
    val b = new ServerBootstrap() {
      channel(classOf[NioServerSocketChannel])
    }
    f(b)
  }

  def newNativeServerChannel(f: ServerBootstrap => Unit) = {
    if (!Epoll.isAvailable) {
      throw MissingEpollSupport()
    }

    val b = new ServerBootstrap() {
      channel(classOf[EpollServerSocketChannel])
    }
    f(b)
  }

  case class MissingEpollSupport() extends Exception("Missing support for native Epoll transport. Please use newServerChannel() or newNIOServerChannel() instead.")

  def localAddress(port: Int)(implicit bootstrap: ServerBootstrap): Unit =
    localAddress(new InetSocketAddress(port))

  def localAddress(address: SocketAddress)(implicit bootstrap: ServerBootstrap): Unit =
    bootstrap.localAddress(address)

  def keepAlive(enabled: Boolean = FALSE)(implicit bootstrap: ServerBootstrap): Unit =
    option(ChannelOption.SO_KEEPALIVE, enabled)

  def tcpNoDelay(enabled: Boolean = TRUE)(implicit bootstrap: ServerBootstrap): Unit =
    option(ChannelOption.TCP_NODELAY, enabled)

  def option[T](option: ChannelOption[T], value: T)(implicit bootstrap: ServerBootstrap): Unit =
    bootstrap.option(option, value)

  val TRUE = java.lang.Boolean.TRUE
  val FALSE = java.lang.Boolean.FALSE
}
