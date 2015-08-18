var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {
	var db = req.db;
	db.collection('users').find().toArray(function (err, items) {
		if(items!==undefined){
			for(var i =0; i<items.length;i++){
				items[i].id = items[i]._id;
				console.log(items[i].id);
			}
		}
        res.json(items);
    });
});

router.post('/', function(req, res, next) {
	var db = req.db;
	console.log("Inserting...");
	db.collection('users').insert(req.body, function(err, result){
		console.log("Insert done");
        res.send(
            (err === null) ? { msg: '' } : { msg: err }
        );
    });	
});

router.put('/', function(req, res, next) {
	var db = req.db;
	console.log("Updating...");
	db.collection('users').updateById(req.body._id.toString(), {$set: req.body});
});

	
router.delete('/:id', function(req, res) {
    var db = req.db;
    db.collection('users').removeById(req.params.id, function(err, result) {
        res.send((result === 1) ? { msg: '' } : { msg:'error: ' + err });
    });
});

/* Get user. */
router.get('/:id', function(req, res) {
	console.log("Here with id: "+req.params.id);
	var db = req.db;
	db.collection('users').findById(req.params.id, function (err, item) {
		if(item===undefined){
			res.statusCode = 404;
			return res.send('Error 404: No quote found');
		}
		item.id = item._id;
        res.json(item);
    });
  
});


module.exports = router;
