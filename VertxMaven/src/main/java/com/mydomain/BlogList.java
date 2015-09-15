package com.mydomain;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydomain.infra.ServicesFactory;
import com.mysocial.model.Blog;
import com.mysocial.model.BlogDTO;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class BlogList implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext routingContext) {
		System.out.println("Thread BlogList: " + Thread.currentThread().getId());
		HttpServerResponse response = routingContext.response();
		response.putHeader("content-type", "application/json");
		Datastore dataStore = ServicesFactory.getMongoDB();

		// For tag search
		String tagParam = routingContext.request().query();
		List<Blog> blogs = null;
		if (tagParam != null) {
			String tagValue = tagParam.split("=")[1];
			blogs = dataStore.createQuery(Blog.class).field("tags").contains(tagValue).asList();
		} else {
			blogs = dataStore.createQuery(Blog.class).asList();
		}
		if (blogs.size() != 0) {
			List<BlogDTO> obj = new ArrayList<BlogDTO>();
			for (Blog b : blogs) {
				BlogDTO dto = new BlogDTO().fillFromModel(b);
				obj.add(dto);
			}

			ObjectMapper mapper = new ObjectMapper();
			try {
				response.end(mapper.writeValueAsString(obj));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			response.setStatusCode(404).end("not found");
		}
	}
}
