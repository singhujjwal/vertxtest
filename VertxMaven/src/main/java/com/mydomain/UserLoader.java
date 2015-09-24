package com.mydomain;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydomain.infra.ServicesFactory;
import com.mysocial.model.User;
import com.mysocial.model.UserDTO;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;


class UserLoader implements Handler<RoutingContext> {
	public void handle(RoutingContext routingContext) {
		System.out.println("Thread UserLoader: "
				+ Thread.currentThread().getId());
		// This handler will be called for every request
		HttpServerResponse response = routingContext.response();
		String id = routingContext.request().getParam("id");
		System.out.println("ID is " + id);
		
		//If signed in is being checked
		if("signedIn".equalsIgnoreCase(id)){
			Session session = routingContext.session();
			String userName = session.get("user");
			if(userName != null && !"".equalsIgnoreCase(userName.trim())){
				System.out.println("User already logged in " + userName);
				response.setStatusCode(200).end("Already Logged in");
				return;
			}else{
				System.out.println("No User already logged in " + userName);
				response.setStatusCode(404).end("Please login");
				return;
			}
		} else {

			Datastore dataStore = ServicesFactory.getMongoDB();
			ObjectId oid = null;
			try {
				oid = new ObjectId(id);
			} catch (Exception e) {// Ignore format errors
				System.out.println("Exception occurred as " + e);
				// e.printStackTrace();
			}
			List<User> users = dataStore.createQuery(User.class).field("id").equal(oid).asList();
			if (users.size() != 0) {
				response.putHeader("content-type", "application/json");
				UserDTO dto = new UserDTO().fillFromModel(users.get(0));
				ObjectMapper mapper = new ObjectMapper();
				JsonNode node = mapper.valueToTree(dto);
				response.end(node.toString());
			} else {
				response.setStatusCode(404).end("not found");
			}

		}
	}
}
