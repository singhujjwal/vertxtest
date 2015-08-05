package com.vertex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

public class HttpVerticle2 extends AbstractVerticle {

	public void start(Future<Void> startFuture) {
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(new Handler<HttpServerRequest>() {

			public void handle(HttpServerRequest req) {
				// TODO Auto-generated method stub
				req.response().end("Hello World!");
				vertx.eventBus().consumer("Channel1", new Handler<Message<String>>() {
					public void handle(Message<String> msg){
						System.out.println("The message =" + msg.body());
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

}
