package com.mydomain;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.w3c.dom.Comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydomain.infra.ServicesFactory;
import com.mysocial.model.Blog;
import com.mysocial.model.CommentDTO;
import com.mysocial.model.User;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

class CommentPersister implements Handler<RoutingContext> {
    public void handle(RoutingContext routingContext) {
        System.out.println("Thread CommentPersister: "
                + Thread.currentThread().getId());
        HttpServerResponse response = routingContext.response();
        String blogId = routingContext.request().getParam("id");
        Session session = routingContext.session();
        response.putHeader("content-type", "application/json");
        routingContext.request().bodyHandler(new Handler<Buffer>() {
            public void handle(Buffer buf) {
                String json = buf.toString("UTF-8");
                ObjectMapper mapper = new ObjectMapper();
                Datastore dataStore = ServicesFactory.getMongoDB();
                CommentDTO dto = null;
                try {
                    dto = mapper.readValue(json, CommentDTO.class);
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
                Comment comment = (Comment) dto.toModel();
                
                ObjectId oid = null;
                try {
                    oid = new ObjectId(blogId);
                } catch (Exception e) {// Ignore format errors
                }
                Blog blog = dataStore.createQuery(Blog.class).field("id")
                        .equal(oid).get();
                List<com.mysocial.model.Comment> comments = blog.getComments();
                comments.add((com.mysocial.model.Comment) comment);
                blog.setComments(comments);
                dataStore.save(blog);
                
                
                /*List<Blog> blogs = user.getUserBlogs();
                blogs.add(blog);
                user.setUserBlogs(blogs);
                dataStore.save(user);*/
                response.setStatusCode(204).end("Comment saved !!");
            };
        });
    }
}
