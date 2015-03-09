var davis = require("./super_stations/davis");
var Q = require("q");

module.exports = {
	code: "empuriabrava",
	fetch: function(){
		var defer = new Q.defer();
		davis("trastitu").then(function(data){
			data.dir = Math.floor(data.dir + 180) % 360;
			defer.resolve(data);
		}, function(err){
			defer.reject(err);
		});

		return defer.promise;
	}
}