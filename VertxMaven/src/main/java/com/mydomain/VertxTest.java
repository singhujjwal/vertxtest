package com.mydomain;

import org.apache.log4j.Logger;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class VertxTest {
	
	public static Logger Log  = Logger.getLogger(VertxTest.class);

	//Send a request as 
	//localhost:9080?type=user
	//With json body {"hello":"World!!"}
	//from postman as a POST request
	public static void main(String[] args) throws Exception {
		Log.info("Starting the Main Ujjwal");
		// VertxOptions options = new VertxOptions().setWorkerPoolSize(10);
		System.out.println("Thread1: "+ Thread.currentThread().getId());
		
		Vertx vertx = Vertx.vertx();// options);
		
//		vertx.deployVerticle(
//				"com.mydomain.MyVerticle",
//				res -> {
//					if (res.succeeded()) {
//						System.out.println("Thread2: "+ Thread.currentThread().getId());
//						System.out.println("Deployment id is: " + res.result());
//					} else {
//						System.out.println("Deployment failed!");
//					}
//				});
//		
//		vertx.deployVerticle(new HttpServerVerticle());
//		vertx.deployVerticle(new HttpProcessor());
		
		
		DeploymentOptions options = new DeploymentOptions().setInstances(10);
		vertx.deployVerticle("com.mydomain.RouterVerticle", options);
//		Thread.sleep(3000);
//		vertx.deployVerticle("com.mydomain.MyVerticle2",
//				new Handler<AsyncResult<String>>() {
//					@Override
//					public void handle(AsyncResult<String> stringAsyncResult) {
//						System.out.println("Thread3: "+ Thread.currentThread().getId());
//						System.out.println("Verticle2 deployment complete");
//					}
//				});
		Log.info("Deployment fired");
	}
}
