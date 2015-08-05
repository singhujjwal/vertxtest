package com.mydomain;

import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class VertxTest {

	public static void main(String[] args) throws Exception {
		// VertxOptions options = new VertxOptions().setWorkerPoolSize(10);
		System.out.println("Thread1: "+ Thread.currentThread().getId());
		Vertx vertx = Vertx.vertx();// options);
		vertx.deployVerticle(
				"com.mydomain.MyVerticle",
				res -> {
					if (res.succeeded()) {
						System.out.println("Thread2: "+ Thread.currentThread().getId());
						System.out.println("Deployment id is: " + res.result());
					} else {
						System.out.println("Deployment failed!");
					}
				});
		vertx.deployVerticle(new HttpServerVerticle());
		vertx.deployVerticle(new HttpProcessor());
		DeploymentOptions options = new DeploymentOptions().setInstances(10);
		vertx.deployVerticle("com.mydomain.RouterVerticle", options);
		Thread.sleep(3000);
		vertx.deployVerticle("com.mydomain.MyVerticle2",
				new Handler<AsyncResult<String>>() {
					@Override
					public void handle(AsyncResult<String> stringAsyncResult) {
						System.out.println("Thread3: "+ Thread.currentThread().getId());
						System.out.println("Verticle2 deployment complete");
					}
				});
		System.out.println("Deployment fired");
	}
}
