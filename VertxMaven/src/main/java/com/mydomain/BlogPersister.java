package com.mydomain;

import java.io.IOException;
import java.util.Date;

import org.mongodb.morphia.Datastore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydomain.infra.ServicesFactory;
import com.mysocial.model.Blog;
import com.mysocial.model.BlogDTO;
import com.mysocial.model.User;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

class BlogPersister implements Handler<RoutingContext> {
    public void handle(RoutingContext routingContext) {
        System.out.println("Thread BlogPersister: "
                + Thread.currentThread().getId());
        HttpServerResponse response = routingContext.response();
        Session session = routingContext.session();
        routingContext.request().bodyHandler(new Handler<Buffer>() {
            public void handle(Buffer buf) {
                String json = buf.toString("UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                Datastore dataStore = ServicesFactory.getMongoDB();
                BlogDTO dto = null;
                try {
                    dto = mapper.readValue(json, BlogDTO.class);
                    String userName = session.get("user");
                    if (userName == null || userName.equals(""))
                        userName = "ash";
                    User user = dataStore.createQuery(User.class).field("userName")
                                    .equal(userName).get();
                    dto.setUserFirst(user.getFirst());
                    dto.setUserLast(user.getLast());
                    dto.setUserId(user.getId().toString());
                    dto.setDate(new Date().getTime());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Blog blog = dto.toModel();
                dataStore.save(blog);
                /*List<Blog> blogs = user.getUserBlogs();
                blogs.add(blog);
                user.setUserBlogs(blogs);
                dataStore.save(user);*/
                response.setStatusCode(204).end("Blog saved !!");
            };
        });
    }
}
