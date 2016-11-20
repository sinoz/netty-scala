# Scala DSL for Netty
A tiny layer on top of Netty to provide a small Scala based networking DSL to make the lives of Scala developers a tiny bit easier.

### How to use
The goal behind this DSL is to make all of your code as compact as possible so you do not have to worry about having tens of different classes to achieve your goal. In this case, the local or remote networking within your application. Currently, the DSL only provides ways to establish a server channel. Support for clients is coming soon.

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

TODO: the rest