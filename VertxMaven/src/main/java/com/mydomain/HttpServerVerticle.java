package com.mydomain;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class HttpServerVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) {
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(request -> {
			String type = request.getParam("type");
			System.out.println("Request type: "+type);
			if(type!=null && type.equals("user")){
				request.bodyHandler(buffer -> {
					System.out.println("Thread6: "+ Thread.currentThread().getId());
					System.out.println("Body handler "+buffer);
					Map<String,Object> dataMap = new HashMap<>();
					dataMap.put("buffer", buffer.toString("UTF-8"));
					JsonObject obj = new JsonObject(dataMap);
					vertx.eventBus().publish("userChannel", obj);
				});
			}
			request.response().end("Processing started");
		});
		
		server.listen(9080, "localhost", res -> {
			if (res.succeeded()) {
				System.out.println("Thread7: "+ Thread.currentThread().getId());
				System.out.println("Server is now listening!");
			} else {
				System.out.println("Failed to bind!");
			}
		});
		startFuture.complete();
	}

	@Override
	public void stop(Future stopFuture) throws Exception {
		System.out.println("MyVerticle stopped!");
		stopFuture.complete();
	}

}
