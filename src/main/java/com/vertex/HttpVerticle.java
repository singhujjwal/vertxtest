package com.vertex;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

public class HttpVerticle extends AbstractVerticle {

	public void start(Future<Void> startFuture) {
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(new Handler<HttpServerRequest>() {

			public void handle(HttpServerRequest req) {
				// TODO Auto-generated method stub
				req.response().end("Hello World!");

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
		vertx.deployVerticle("com.vertex.HttpVerticle");

	}

}
