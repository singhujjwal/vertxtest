package com.mydomain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mydomain.infra.ServicesFactory;
import com.mysocial.model.User;
import com.mysocial.model.UserDTO;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.BasicAuthHandler;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import scala.util.parsing.json.JSON;
import scala.util.parsing.json.JSONObject;

public class RouterVerticle extends AbstractVerticle {
	
	private static List<ServerWebSocket> allConnectedSockets = new ArrayList<>();
	@Override
	public void start(Future<Void> startFuture) throws Exception {

		HttpServer server = vertx.createHttpServer();
		
		Router router = Router.router(vertx);
		
		router.route().handler(CookieHandler.create());

		router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

		AuthProvider ap = new MyAuthProvier();

		router.route().handler(UserSessionHandler.create(ap));

		AuthHandler basicAuthHandler = BasicAuthHandler.create(ap);

		// Send http://localhost:8080/private as POST and without body
		// you will get Unauthorized
		
		router.route("/private/*").handler(basicAuthHandler);
		
		router.route("/private/*").handler(new Handler<RoutingContext>() {
			@Override
			public void handle(RoutingContext rc) {
				System.out.println("Handler: " + rc.user().principal());
				rc.response().end("Done");
			}
		});
		
		server = server.websocketHandler(serverWebSocket -> {
			//Got a new connection
			System.out.println("Connected: "+serverWebSocket.remoteAddress());
			//Store new connection in list
			allConnectedSockets.add(serverWebSocket);
			//Setup handler to receive the data
			serverWebSocket.handler( handler ->{
				String message = new String(handler.getBytes());
				System.out.println("message: "+ message);
				JsonObject json = new JsonObject(message);
				JsonObject jsonToSend = new JsonObject();
				
				
				jsonToSend.put("text", json.getString("data"));
				jsonToSend.put("messageType", "chatMessage");
				jsonToSend.put("sender", "okbye");
				
				//Now broadcast received message to all other clients
				for(ServerWebSocket sock : allConnectedSockets){
					System.out.println("Sending message to client...");
					sock.writeFinalTextFrame(jsonToSend.toString());
				}
			});
			//Register handler to remove connection from list when connection is closed
			serverWebSocket.closeHandler(handler->{
				System.out.println("Closing connection: " + serverWebSocket.remoteAddress());
				allConnectedSockets.remove(serverWebSocket);
			});
			
		});

		router.get("/services/users/:id").handler(new UserLoader());
		router.post("/services/users").handler(new UserPersister());
		
		
		router.get("/Services/rest/user/:id").handler(new UserLoader());
		router.post("/Services/rest/user/register/").handler(new UserPersister());
		router.post("/Services/rest/user/auth").handler(new AuthenticateUser());
		
		
		router.get("/logout/").handler(routingContext -> {
			routingContext.session().destroy();
			System.out.println("Destroyed the session");
			HttpServerResponse response = routingContext.response();
			// enable chunked responses because we will be adding data as
			// we execute over other handlers. This is only required once and
			// only if several handlers do output.
			response.setChunked(true);
			response.write("Logged out Successfully");
			// Now end the response
			routingContext.response().end();
		});
		
		router.get("/Services/rest/blogs").handler(new BlogList());
        router.post("/Services/rest/blogs/:id/comments").handler(new CommentPersister());     
        router.post("/Services/rest/user/register").handler(new UserPersister());
        router.post("/Services/rest/blogs").handler(new BlogPersister());
		router.route("/*").handler(StaticHandler.create("webroot").setCachingEnabled(false));
        server.requestHandler(router::accept).listen(8080);
        System.out.println("Thread Router Start: "
                + Thread.currentThread().getId());
        System.out.println("STARTED ROUTER");
        startFuture.complete(); 
        
	}
	
	public static void sendNewUserInfo(User u) {
		for(ServerWebSocket sock : allConnectedSockets){
			System.out.println("Sending User to client...");
			JsonObject userInfoMsg = new JsonObject();
			JsonObject userInfo = new JsonObject();
			userInfo.put("first", u.getFirst());
			userInfo.put("last", u.getLast());
			userInfo.put("username", u.getUserName());
			/*ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.valueToTree(u);*/
			userInfoMsg.put("messageType", "UserLogin");
			userInfoMsg.put("messageObject", userInfo);
			System.out.println("New User msg: " + userInfoMsg.toString());
			sock.writeFinalTextFrame(userInfoMsg.toString());
		}
	}
	
	public static void main(String[] args)
    {
        VertxOptions options = new VertxOptions().setWorkerPoolSize(10);
        Vertx vertx = Vertx.vertx(options);
        vertx.deployVerticle("com.mydomain.RouterVerticle");
    }

}


class GraphLoader implements Handler<RoutingContext> {
	@Override
	public void handle(RoutingContext arg0) {
		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
		File f = new File("/Users/maruthir/Documents/Training/neo4jdb");
		GraphDatabaseService db = dbFactory.newEmbeddedDatabase(f);
		try (Transaction tx = db.beginTx()) {
			// Perform DB operations
			tx.success();
		}
	}
}

	class BlogSearch implements Handler<RoutingContext> {
	    public void handle(RoutingContext routingContext) {
	        System.out.println("Thread BlogSearch: "
	                + Thread.currentThread().getId());
	        HttpServerResponse response = routingContext.response();
	        String id = routingContext.request().getParam("id");

	        response.putHeader("content-type", "application/json");
	        Datastore dataStore = ServicesFactory.getMongoDB();
	        ObjectId oid = null;
	        try {
	            oid = new ObjectId(id);
	        } catch (Exception e) {// Ignore format errors
	        }
	        List<User> users = dataStore.createQuery(User.class).field("id")
	                .equal(oid).asList();
	        if (users.size() != 0) {
	            UserDTO dto = new UserDTO().fillFromModel(users.get(0));
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode node = mapper.valueToTree(dto);
	            response.end(node.toString());
	        } else {
	            response.setStatusCode(404).end("not found");
	        }
	    }
	}

