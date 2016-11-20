package netty_scala_dsl

import java.net.{InetSocketAddress, SocketAddress}

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.epoll.{Epoll, EpollEventLoopGroup, EpollServerSocketChannel}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel._

/** A mixin that provides flexible functions that form the networking DSL. */
trait NettyDSL extends NettyAliases {

  def newServerChannel(f: ServerBootstrap => Unit) = Epoll.isAvailable match {
    case true => newNativeServerChannel(f)
    case false => newNIOServerChannel(f)
  }

  def newNIOServerChannel(configure: ServerBootstrap => Unit) = {
    val b = new ServerBootstrap() {
      channel(classOf[NioServerSocketChannel])
      group(new NioEventLoopGroup(), new NioEventLoopGroup())
      childHandler(DefaultEventHandler)
      configure(this)
    }

    b.bind().sync().channel()
  }

  def newNativeServerChannel(configure: ServerBootstrap => Unit) = {
    if (!Epoll.isAvailable) {
      throw MissingEpollSupport()
    }

    val b = new ServerBootstrap() {
      channel(classOf[EpollServerSocketChannel])
      group(new EpollEventLoopGroup(), new EpollEventLoopGroup())
      childHandler(DefaultEventHandler)
      configure(this)
    }

    b.bind().sync().channel()
  }

  case class MissingEpollSupport() extends Exception("Missing support for native Epoll transport. Please use newServerChannel() or newNIOServerChannel() instead.")

  def localAddress(port: Int)(implicit bootstrap: ServerBootstrap): Unit =
    localAddress(new InetSocketAddress(port))

  def localAddress(address: SocketAddress)(implicit bootstrap: ServerBootstrap): Unit =
    bootstrap.localAddress(address)

  def keepAlive(enabled: java.lang.Boolean = FALSE)(implicit bootstrap: ServerBootstrap): Unit =
    option(ChannelOption.SO_KEEPALIVE, enabled)

  def tcpNoDelay(enabled: java.lang.Boolean = TRUE)(implicit bootstrap: ServerBootstrap): Unit =
    option(ChannelOption.TCP_NODELAY, enabled)

  def option[T](option: ChannelOption[T], value: T)(implicit bootstrap: ServerBootstrap): Unit =
    bootstrap.option(option, value)

  def initializer(init: ChannelPipeline => Unit)(implicit bootstrap: ServerBootstrap): Unit =
    remoteChannelHandler(new ChannelInitializer[SocketChannel] {
      override def initChannel(ch: SocketChannel): Unit = {
        init(ch.pipeline())
      }
    })

  def remoteChannelHandler(channelHandler: ChannelHandler)(implicit bootstrap: ServerBootstrap): Unit =
    bootstrap.childHandler(channelHandler)

  @Sharable
  object DefaultEventHandler extends ChannelInboundHandlerAdapter {
    @throws[Exception]
    override def channelRead(ctx: ChannelHandlerContext, msg: Message): Unit = {
      // TODO
    }

    @throws[Exception]
    override def exceptionCaught(ctx: ChannelHandlerContext, cause: Failure): Unit = {
      cause.printStackTrace()
    }
  }

  val TRUE = java.lang.Boolean.TRUE
  val FALSE = java.lang.Boolean.FALSE
}