package com.fredpolicarpo.hello_microservice_message_driven;

import io.netty.util.internal.StringUtil;
import io.vertx.core.json.JsonObject;
import io.vertx.core.AbstractVerticle;

public class HelloMicroService extends AbstractVerticle {

    @Override
    public void start() {
        System.out.println("Starting the handler for the producer...");
        vertx.eventBus().consumer("hello", message -> {
            final var chaos = Math.random();

            final var json = new JsonObject()
                    .put("served-by", this.toString());

            if (chaos < 0.6) {
                if (StringUtil.isNullOrEmpty(message.body().toString())) {
                    message.reply(json.put("message", "hello"));
                } else {
                    message.reply(json.put("message", "hello " + message.body()));
                }
            } else if (chaos < 0.9) {
                System.out.println("Returning a failure");
                message.fail(500, "message processing failure");
            } else {
                System.out.println("Not replying");
            }
        });
    }
}
