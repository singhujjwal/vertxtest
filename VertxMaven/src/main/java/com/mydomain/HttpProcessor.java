package com.mydomain;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;


public class HttpProcessor extends AbstractVerticle {
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		vertx.eventBus().consumer("userChannel", message -> {
			//Body is a buffer
			System.out.println("In the http processor this is what I got");
            System.out.println("message.body() = "
                + message.body());
            String buf = ((JsonObject)message.body()).getString("buffer");
            System.out.println(buf);
            
        });
		startFuture.complete();
	}
}
