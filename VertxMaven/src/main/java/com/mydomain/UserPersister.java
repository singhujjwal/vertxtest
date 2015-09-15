package com.mydomain;

import java.io.IOException;

import org.mongodb.morphia.Datastore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydomain.infra.ServicesFactory;
import com.mysocial.model.User;
import com.mysocial.model.UserDTO;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

class UserPersister implements Handler<RoutingContext> {
	public void handle(RoutingContext routingContext) {
		System.out.println("Thread UserPersister: "
				+ Thread.currentThread().getId());
		// This handler will be called for every request
		HttpServerResponse response = routingContext.response();
		routingContext.request().bodyHandler(new Handler<Buffer>() {
			public void handle(Buffer buf) {
				String json = buf.toString("UTF-8");
				ObjectMapper mapper = new ObjectMapper();
				UserDTO dto = null;
				try {
					dto = mapper.readValue(json, UserDTO.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				User u = dto.toModel();
				Datastore dataStore = ServicesFactory.getMongoDB();
				dataStore.save(u);
				System.out.println("Saving user " + u.getUserName() + " and password: " + u.getPassword());
				response.setStatusCode(204).end("Data saved");
			};
		});
	}
}
