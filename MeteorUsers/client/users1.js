


console.log("Running client code...");
Session.set("formStyle", "display: none;");
var users = new Meteor.Collection("users");

var UserFormElementProto = Object.create(HTMLFormElement.prototype);
//Define the html of the element
UserFormElementProto.createdCallback = function() {
   this.innerHTML = '<form class="userForm"><input type="text" name="name" placeholder="User Name" /><input type="number" name="age" placeholder="Age" /><input type="submit" value="Save" id="userSave"></form>';
};

var UserFormElement = document.registerElement('user-form', {prototype: UserFormElementProto});

Template.body.helpers({

});

Template.userList.helpers({
	users : function() {
		return users.find();
	},
	formStyle : function() {
		return Session.get("formStyle");
	}
});

Template.userList.events({
	'click  #addUserButton' : function() {
		console.log("Showing add form");
		Session.set("formStyle", "display: block;");
	},
	'submit .userForm' : function(event) {
		console.log("Adding an user");
		event.preventDefault();
		var name = event.target.name.value;
		var age = event.target.age.value;
		users.insert({
			name : name,
			age : age,
			createdAt : new Date()
		});
		event.target.name.value="";
		event.target.age.value="";
		Session.set("formStyle", "display: none;");
	},
	'click  .userDelete' : function() {
		console.log("deleting user");
		users.remove(this._id);
	},
});
