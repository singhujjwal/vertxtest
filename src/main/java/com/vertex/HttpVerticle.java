package com.vertex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

public class HttpVerticle extends AbstractVerticle {

	public void start(Future<Void> startFuture) {
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(new Handler<HttpServerRequest>() {

			public void handle(HttpServerRequest req) {
				// TODO Auto-generated method stub
				//req.response().end("Hello World!");
				
				req.bodyHandler(new Handler<Buffer>() {
					public void handle(Buffer buff){
						EventBus eb = vertx.eventBus();
						//This vertex published whatever it gets from the request buffer
						//eb.publish("Channel1", "Hello can i board the bus");
						eb.publish("Channel1", buff.toString());
					}
				});
			}
		});

		server.listen(8080, "localhost",
				new Handler<AsyncResult<HttpServer>>() {
					public void handle(AsyncResult<HttpServer> res) {
						//System.out.println(res.result().toString());
					}
				});

		System.out.println("MyVerticle Started!");
		startFuture.complete();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		VertxOptions options = new VertxOptions().setWorkerPoolSize(10);
		Vertx vertx = Vertx.vertx(options);
		
		
		vertx.eventBus().publish("Channel1", "message2");
		
		
//		vertx.deployVerticle("com.vertex.HttpVerticle");
//		vertx.deployVerticle("com.vertex.HttpVerticle2");
		vertx.deployVerticle("com.mydomain.RouterVerticle");
		
	}

}
