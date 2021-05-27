package com.fredpolicarpo.hello_microservice_http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class HelloMicroService extends AbstractVerticle {
    @Override
    public void start() {
        final var router = Router.router(vertx);

        router.get("/").handler(this::hello);
        router.get("/:name").handler(this::hello);

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
    }

    private void hello(RoutingContext routingContext) {
        var msg = "Hello";

        if (routingContext.pathParam("name") != null) {
            msg += routingContext.pathParam("name");
        }

        final var json = new JsonObject().put("message", msg);

        routingContext.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .end(json.encode());
    }
}
