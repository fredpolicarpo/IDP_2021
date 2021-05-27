package com.fredpolicarpo.hello_vertx;

import io.vertx.rxjava.core.AbstractVerticle;

public class MyFirstRXVerticle extends AbstractVerticle {
  @Override
  public void start() {
    final var server = vertx.createHttpServer();

    server.requestStream()
      .toObservable()
      .subscribe(req ->
        req.response().end("Hello from " + Thread.currentThread().getName())
      );

    server.rxListen(8888).subscribe();
  }
}
