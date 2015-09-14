package com.mydomain;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;

public class RouterVerticle extends AbstractVerticle {
	@Override
	public void start(Future<Void> startFuture) throws Exception {

		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		
		router.route("/*").handler(StaticHandler.create("webroot").setCachingEnabled(false));
		
		


//		router.route().handler(CookieHandler.create());

//		router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
//		AuthProvider ap = new MyAuthProvier();
//
//		router.route().handler(UserSessionHandler.create(ap));
//
//		AuthHandler basicAuthHandler = BasicAuthHandler.create(ap);
//
//		// Send http://localhost:8080/private as POST and without body
//		// you will get Unauthorized
//		
//
//		router.route("/").handler(StaticHandler.create().setWebRoot("webapp"));
		
//		router.route().handler(routingContext -> {
//
//			  // This handler will be called for every request
//			  HttpServerResponse response = routingContext.response();
//			  response.putHeader("content-type", "text/plain");
//
//			  // Write to the response and end it
//			  response.end("Hello World from Vert.x-Web!");
//		});

		
//		router.route("/private/*").handler(basicAuthHandler);
//		
//		router.route("/private/*").handler(new Handler<RoutingContext>() {
//			@Override
//			public void handle(RoutingContext rc) {
//				System.out.println("Handler: " + rc.user().principal());
//				rc.response().end("Done");
//			}
//		});

		//router.get("/services/users/:id").handler(new UserLoader());
		// router.post("/services/users").handler(new UserPersister());

//		router.post("/Services/rest/user/register/").handler(new UserPersister());

		//router.route().handler(StaticHandler.create().setCachingEnabled(false));
		 //Add handler for static files

//		router.get("/Services/rest/blogs").handler(new BlogList());
//        router.post("/Services/rest/blogs/:id/comments").handler(new CommentPersister());
//        
//        router.post("/Services/rest/user/register").handler(new UserPersister());
//        router.post("/Services/rest/user/auth").handler(new AuthenticateUser());
//        router.post("/Services/rest/blogs").handler(new BlogPersister());

        server.requestHandler(router::accept).listen(8080);
        System.out.println("Thread Router Start: "
                + Thread.currentThread().getId());
        System.out.println("STARTED ROUTER");
        startFuture.complete(); 
        
	}
	
	public static void main(String[] args)
    {
        VertxOptions options = new VertxOptions().setWorkerPoolSize(10);
        Vertx vertx = Vertx.vertx(options);
        vertx.deployVerticle("com.mydomain.RouterVerticle");
    }

}

class MyAuthProvier implements AuthProvider {

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

//class GraphLoader implements Handler<RoutingContext> {
//	@Override
//	public void handle(RoutingContext arg0) {
//		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
//		File f = new File("/Users/maruthir/Documents/Training/neo4jdb");
//		GraphDatabaseService db = dbFactory.newEmbeddedDatabase(f);
//		try (Transaction tx = db.beginTx()) {
//			// Perform DB operations
//			tx.success();
//		}
//	}
//}
//
//class UserPersister implements Handler<RoutingContext> {
//	public void handle(RoutingContext routingContext) {
//		System.out.println("Thread UserPersister: "
//				+ Thread.currentThread().getId());
//		// This handler will be called for every request
//		HttpServerResponse response = routingContext.response();
//		routingContext.request().bodyHandler(new Handler<Buffer>() {
//			public void handle(Buffer buf) {
//				String json = buf.toString("UTF-8");
//				ObjectMapper mapper = new ObjectMapper();
//				UserDTO dto = null;
//				try {
//					dto = mapper.readValue(json, UserDTO.class);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				User u = dto.toModel();
//				Datastore dataStore = ServicesFactory.getMongoDB();
//				dataStore.save(u);
//				response.setStatusCode(204).end("Data saved");
//			};
//		});
//	}
//}
//
//class UserLoader implements Handler<RoutingContext> {
//	public void handle(RoutingContext routingContext) {
//		System.out.println("Thread UserLoader: "
//				+ Thread.currentThread().getId());
//		// This handler will be called for every request
//		HttpServerResponse response = routingContext.response();
//		String id = routingContext.request().getParam("id");
//		System.out.println("ID is " + id);
//
//		
//		Datastore dataStore = ServicesFactory.getMongoDB();
//		ObjectId oid = null;
//		try {
//			oid = new ObjectId(id);
//		} catch (Exception e) {// Ignore format errors
//			System.out.println("Exception occurred as " + e);
//			//e.printStackTrace();
//		}
//		List<User> users = dataStore.createQuery(User.class).field("id")
//				.equal(oid).asList();
//		if (users.size() != 0) {
//			response.putHeader("content-type", "application/json");
//			UserDTO dto = new UserDTO().fillFromModel(users.get(0));
//			ObjectMapper mapper = new ObjectMapper();
//			JsonNode node = mapper.valueToTree(dto);
//			response.end(node.toString());
//		} else {
//			response.setStatusCode(404).end("not found");
//		}
//	}
//	
//	class BlogPersister implements Handler<RoutingContext> {
//	    public void handle(RoutingContext routingContext) {
//	        System.out.println("Thread BlogPersister: "
//	                + Thread.currentThread().getId());
//	        HttpServerResponse response = routingContext.response();
//	        Session session = routingContext.session();
//	        routingContext.request().bodyHandler(new Handler<Buffer>() {
//	            public void handle(Buffer buf) {
//	                String json = buf.toString("UTF-8");
//	                ObjectMapper mapper = new ObjectMapper();
//	                Datastore dataStore = ServicesFactory.getMongoDB();
//	                BlogDTO dto = null;
//	                try {
//	                    dto = mapper.readValue(json, BlogDTO.class);
//	                    String userName = session.get("user");
//	                    if (userName == null || userName.equals(""))
//	                        userName = "ash";
//	                    User user = dataStore.createQuery(User.class).field("userName")
//	                                    .equal(userName).get();
//	                    dto.setUserFirst(user.getFirst());
//	                    dto.setUserLast(user.getLast());
//	                    dto.setUserId(user.getId().toString());
//	                    dto.setDate(new Date().getTime());
//	                } catch (IOException e) {
//	                    e.printStackTrace();
//	                }
//	                Blog blog = dto.toModel();
//	                dataStore.save(blog);
//	                /*List<Blog> blogs = user.getUserBlogs();
//	                blogs.add(blog);
//	                user.setUserBlogs(blogs);
//	                dataStore.save(user);*/
//	                response.setStatusCode(204).end("Blog saved !!");
//	            };
//	        });
//	    }
//	}
//
//	class CommentPersister implements Handler<RoutingContext> {
//	    public void handle(RoutingContext routingContext) {
//	        System.out.println("Thread CommentPersister: "
//	                + Thread.currentThread().getId());
//	        HttpServerResponse response = routingContext.response();
//	        String blogId = routingContext.request().getParam("id");
//	        Session session = routingContext.session();
//	        response.putHeader("content-type", "application/json");
//	        routingContext.request().bodyHandler(new Handler<Buffer>() {
//	            public void handle(Buffer buf) {
//	                String json = buf.toString("UTF-8");
//	                ObjectMapper mapper = new ObjectMapper();
//	                Datastore dataStore = ServicesFactory.getMongoDB();
//	                CommentDTO dto = null;
//	                try {
//	                    dto = mapper.readValue(json, CommentDTO.class);
//	                    String userName = session.get("user");
//	                    if (userName == null || userName.equals(""))
//	                        userName = "ash";
//	                    User user = dataStore.createQuery(User.class).field("userName")
//	                                    .equal(userName).get();
//	                    dto.setUserFirst(user.getFirst());
//	                    dto.setUserLast(user.getLast());
//	                    dto.setUserId(user.getId().toString());
//	                    dto.setDate(new Date().getTime());
//	                } catch (IOException e) {
//	                    e.printStackTrace();
//	                }
//	                Comment comment = (Comment) dto.toModel();
//	                
//	                ObjectId oid = null;
//	                try {
//	                    oid = new ObjectId(blogId);
//	                } catch (Exception e) {// Ignore format errors
//	                }
//	                Blog blog = dataStore.createQuery(Blog.class).field("id")
//	                        .equal(oid).get();
//	                List<com.mysocial.model.Comment> comments = blog.getComments();
//	                comments.add((com.mysocial.model.Comment) comment);
//	                blog.setComments(comments);
//	                dataStore.save(blog);
//	                
//	                
//	                /*List<Blog> blogs = user.getUserBlogs();
//	                blogs.add(blog);
//	                user.setUserBlogs(blogs);
//	                dataStore.save(user);*/
//	                response.setStatusCode(204).end("Comment saved !!");
//	            };
//	        });
//	    }
//	}
//
//	class BlogList implements Handler<RoutingContext> {
//	    public void handle(RoutingContext routingContext) {
//	        System.out.println("Thread BlogList: "
//	                + Thread.currentThread().getId());
//	        HttpServerResponse response = routingContext.response();
//	        response.putHeader("content-type", "application/json");
//	        Datastore dataStore = ServicesFactory.getMongoDB();
//	        
//	        
//	        //For tag search
//	        String tagParam = routingContext.request().query();
//	        List<Blog> blogs = null;
//	        if (tagParam != null){
//	            String tagValue = tagParam.split("=")[1];
//	            blogs = dataStore.createQuery(Blog.class).field("tags").contains(tagValue).asList();
//	        }
//	        else{
//	            blogs = dataStore.createQuery(Blog.class).asList();
//	        }
//	        if (blogs.size() != 0)
//	        {
//	            List<BlogDTO> obj = new ArrayList<BlogDTO>();
//	            for (Blog b : blogs)
//	            {
//	                BlogDTO dto = new BlogDTO().fillFromModel(b);
//	                obj.add(dto);
//	            }
//	            
//	            ObjectMapper mapper = new ObjectMapper();
//	            try
//	            {
//	                response.end(mapper.writeValueAsString(obj));
//	            }
//	            catch (JsonProcessingException e)
//	            {
//	                // TODO Auto-generated catch block
//	                e.printStackTrace();
//	            }
//	        }
//	        else {
//	            response.setStatusCode(404).end("not found");
//	        }
//	    }
//	}
//
//	class AuthenticateUser implements Handler<RoutingContext> {
//	    public void handle(RoutingContext routingContext) {
//	        System.out.println("Thread AuthenticateUser: "
//	                + Thread.currentThread().getId());
//	        
//	        HttpServerResponse response = routingContext.response();
//	        Session session = routingContext.session();
//	        
//	        routingContext.request().bodyHandler(new Handler<Buffer>() {
//	            public void handle(Buffer buf)
//	            {
//	                Datastore dataStore = ServicesFactory.getMongoDB();
//	                String json = buf.toString("UTF-8");
//	                JsonObject jsonObj = new JsonObject(json);
//	                String user = jsonObj.getString("userName");
//	                String passwd = jsonObj.getString("password");
//	                List<User> users = dataStore.createQuery(User.class).field("userName")
//	                                .equal(user).asList();
//	                if (users.size() != 0)
//	                {
//	                    for (User u : users){
//	                        if (u.getPassword().equals(passwd)){
//	                            session.put("user", u.getUserName());
//	                            response.setStatusCode(204).end("User Authentication Success !!!");
//	                            break;
//	                        }
//	                    }
//	                }
//	                else
//	                {
//	                    response.setStatusCode(404).end("not found");
//	                }
//	            };
//	        });
//	    }
//	}
//
//	class BlogSearch implements Handler<RoutingContext> {
//	    public void handle(RoutingContext routingContext) {
//	        System.out.println("Thread BlogSearch: "
//	                + Thread.currentThread().getId());
//	        HttpServerResponse response = routingContext.response();
//	        String id = routingContext.request().getParam("id");
//
//	        response.putHeader("content-type", "application/json");
//	        Datastore dataStore = ServicesFactory.getMongoDB();
//	        ObjectId oid = null;
//	        try {
//	            oid = new ObjectId(id);
//	        } catch (Exception e) {// Ignore format errors
//	        }
//	        List<User> users = dataStore.createQuery(User.class).field("id")
//	                .equal(oid).asList();
//	        if (users.size() != 0) {
//	            UserDTO dto = new UserDTO().fillFromModel(users.get(0));
//	            ObjectMapper mapper = new ObjectMapper();
//	            JsonNode node = mapper.valueToTree(dto);
//	            response.end(node.toString());
//	        } else {
//	            response.setStatusCode(404).end("not found");
//	        }
//	    }
//	}
//
//}

