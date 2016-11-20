# Scala DSL for Netty
A very thin layer on top of Netty to provide a small Scala based networking DSL to make the lives of Scala developers a tiny bit easier.

### How to use
The goal behind this DSL is to make all of your code as compact as possible so you do not have to worry about having tens of different classes to achieve your goal. In this case, the local or remote networking within your application. Currently, the DSL only provides ways to establish a server channel. Support for clients is coming soon.

#### Client Channels
TODO

#### Server Channels
To establish a NIO based server channel:

```
newNIOServerChannel { implicit bootstrap =>
    // ... bootstrap configuration
}
```

Or if you need an Epoll based channel:

```
newNativeServerChannel { implicit bootstrap =>
    // ... bootstrap configuration
}
```

Or if you're the average user that does not want to worry about whether to choose the NIO or native solutions:

```
newServerChannel { implicit bootstrap =>
    // ... bootstrap configuration
}
```

As you've probably noticed, the `bootstrap` parameter is marked `implicit`. This is done on purpose as the DSL provides configuration functions to make your code even clearer. As is demonstrated here:

```
newServerChannel { implicit bootstrap =>
    localAddress(8080)
    tcpNoDelay()
}
```

Please keep in mind that `newServerChannel` and its variants are all synchronous operations and must externally be wrapped into a separate `Future` if you happen to frequently establish connections.

#### Initialization of Remote `SocketChannel`s
Apart from making your life easier, this API also aims for complete freedom in configuring your channels. Netty by default forces you to provide your own child `ChannelHandler`. We do not as this API comes with an internal default placeholder channel event handler. However, if you do wish to customize your own `ChannelInitializer` and subordinate `ChannelHandler`s, you can do so:

```
initializer { implicit pipeline =>
    // ...
}
```

And to add your own handlers or codecs to the pipeline:

```
+=("handler" -> MyChannelEventHandler)
```