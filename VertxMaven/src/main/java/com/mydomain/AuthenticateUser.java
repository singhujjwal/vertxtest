package com.mydomain;

import java.util.List;

import org.mongodb.morphia.Datastore;

import com.mydomain.infra.ServicesFactory;
import com.mysocial.model.User;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;

class AuthenticateUser implements Handler<RoutingContext> {
    public void handle(RoutingContext routingContext) {
        System.out.println("Thread AuthenticateUser: "
                + Thread.currentThread().getId());
        
        HttpServerResponse response = routingContext.response();
        Session session = routingContext.session();
        
        routingContext.request().bodyHandler(new Handler<Buffer>() {
            public void handle(Buffer buf)
            {
                Datastore dataStore = ServicesFactory.getMongoDB();
                String json = buf.toString("UTF-8");
                JsonObject jsonObj = new JsonObject(json);
                String user = jsonObj.getString("userName");
                String passwd = jsonObj.getString("password");
                List<User> users = dataStore.createQuery(User.class).field("userName")
                                .equal(user).asList();
                if (users.size() != 0)
                {
                    for (User u : users){
                        if (u.getPassword().equals(passwd)){
                            session.put("user", u.getUserName());
                            response.setStatusCode(204).end("User Authentication Success !!!");
                            break;
                        }
                    }
                }
                else
                {
                    response.setStatusCode(404).end("not found");
                }
            };
        });
    }
}
