Meteor.startup(function() {
	// code to run on server at startup
	console.log("Server starting");
	var users = new Meteor.Collection("users");
});
