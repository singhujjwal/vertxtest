package com.mydomain;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;

public class MyAuthProvier implements AuthProvider {

	@Override
	public void authenticate(JsonObject json,
			Handler<AsyncResult<io.vertx.ext.auth.User>> handler) {
		System.out.println("Authenticating users with: " + json);
		
		
		AsyncResult<io.vertx.ext.auth.User> result = new AsyncResult<io.vertx.ext.auth.User>() {
			
			public boolean succeeded() {
				return json.getString("username").equals("admin")
						&& json.getString("password").equals("admin123");
			}

			public io.vertx.ext.auth.User result() {
				return new io.vertx.ext.auth.User() {
					public void setAuthProvider(AuthProvider provider) {
						System.out
								.println("Setting auth provider: " + provider);
					}

					public JsonObject principal() {
						Map<String, Object> dataMap = new HashMap<>();
						dataMap.put("buffer", json.getString("username"));
						JsonObject obj = new JsonObject(dataMap);
						return obj;
					}

					public io.vertx.ext.auth.User isAuthorised(String url,
							Handler<AsyncResult<Boolean>> handler) {
						System.out.println("Is authorized call: " + url);
						return this;
					}

					public io.vertx.ext.auth.User clearCache() {
						return null;
					}
				};
			}

			public boolean failed() {
				return !(json.getString("username").equals("admin") && json
						.getString("password").equals("admin123"));
			}

			public Throwable cause() {
				return null;
			}
		};
		handler.handle(result);
	}
}
