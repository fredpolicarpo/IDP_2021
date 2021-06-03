package com.fredpolicarpo.hello_consumer_microservice_message;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpStatusClass;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.eventbus.Message;
import rx.Single;

import java.util.concurrent.TimeUnit;

public class HelloConsumer extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer()
                .requestHandler(req -> {
                    final var eventBus = vertx.eventBus();

                    final var obs1 = eventBus.<JsonObject>rxRequest("hello", "Luke")
                            .subscribeOn(RxHelper.scheduler(vertx))
                            .timeout(3, TimeUnit.SECONDS)
                            .retry()
                            .map(Message::body);

                    final var obs2 = eventBus.<JsonObject>rxRequest("hello", "Leia")
                            .subscribeOn(RxHelper.scheduler(vertx))
                            .timeout(3, TimeUnit.SECONDS)
                            .retry()
                            .map(Message::body);

                    Single.zip(obs1, obs2, (luke, leia) ->
                            new JsonObject()
                                    .put("Luke", luke.getString("message") + " from " + luke.getString("served-by"))
                                    .put("Leia", leia.getString("message") + " from " + luke.getString("served-by"))
                    ).subscribe(
                            x -> req.response().end(x.encodePrettily()),
                            t -> {
                                t.printStackTrace();
                                req.response()
                                        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                                        .end(t.getMessage());
                            }
                    );
                })
                .listen(8082);
    }
}
