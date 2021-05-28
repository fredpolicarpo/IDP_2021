package com.fredpolicarpo.hello_vertx_consumer;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.HttpResponse;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.codec.BodyCodec;
import rx.Single;

public class HelloConsumerMicroService extends AbstractVerticle {
    private WebClient client;

    @Override
    public void start() {
        client = WebClient.create(vertx);

        final var router = Router.router(vertx);

        router.get("/").handler(this::invokeMyFirstMicroservice);

        vertx.createHttpServer().requestHandler(router).listen(8081);
    }

    private void invokeMyFirstMicroservice(RoutingContext routingContext) {
        final var request = client.get(8080, "localhost", "/Luke")
                .as(BodyCodec.jsonObject());

        final var request2 = client.get(8080, "localhost", "/Leia")
                .as(BodyCodec.jsonObject());

        final var s1 = request.rxSend().map(HttpResponse::body);

        final var s2 = request2.rxSend().map(HttpResponse::body);

        Single.zip(s1, s2, (luke, leia) -> new JsonObject()
                .put("Luke", luke.getString("message"))
                .put("Leia", leia.getString("message"))
        ).subscribe(
                res -> routingContext.response().end(res.encodePrettily()),
                error -> {
                    error.printStackTrace();
                    routingContext.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end(error.getMessage());
                });
    }
}