var user = can.Construct.extend({}, {
    somefunction: function(){
    	console.log("Hello there...");
    },
    getName: function() {
    	return this.name;
    }
});

var t = new Todo();
t.allowedToEdit(); // true
