var fs = require("fs");


var stations = [];
fs.readdirSync("./station_modules").forEach(function(file){
	if(file.indexOf(".js") > 0){
		stations.push(require("./station_modules/" + file));
	}
});


stations.forEach(function(station){
	station.fetch().then(function(data){
		console.log(station.code + ": " + data);
	});
});
