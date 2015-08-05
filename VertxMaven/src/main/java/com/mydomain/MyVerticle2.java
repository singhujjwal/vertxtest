package com.mydomain;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MyVerticle2 extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
    	vertx.eventBus().publish("Channel1", "message 2");
        System.out.println("MyVerticle started!");
        startFuture.complete();
    }

    @Override
    public void stop(Future stopFuture) throws Exception {
        System.out.println("MyVerticle stopped!");
        stopFuture.complete();
    }

}
