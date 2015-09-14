if (Meteor.isClient) {

  Template.body.helpers({

  });
  
  Template.userList.helpers({
	users: function () {
      return [{name:'Hari',age:22},{name: 'Kelly', age:42},{name: 'Will', age:46}];
    }
  });

  Template.userList.events({
    'click button': function () {
      // increment the counter when button is clicked
      Session.set('counter', Session.get('counter') + 1);
    }
  });
}

if (Meteor.isServer) {
  Meteor.startup(function () {
    // code to run on server at startup
  });
}
