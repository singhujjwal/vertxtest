package com.mydomain;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;

public class MyVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> startFuture) {
        vertx.eventBus().consumer("Channel1", message -> {
            System.out.println("message.body() = "
                + message.body());
        });
        
        byte[] initialData = new byte[]{1, 2, 3};

        Buffer buffer = Buffer.buffer(initialData);
        buffer.setShort ( 10, (short) 127);
        buffer.appendByte  ((byte)  127);
        System.out.println("MyVerticle started!");
        
        startFuture.complete();
    }

    @Override
    public void stop(Future stopFuture) throws Exception {
        System.out.println("MyVerticle stopped!");
        stopFuture.complete();
        
    }

}
