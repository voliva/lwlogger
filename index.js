var fs = require("fs");

var stationToRun = process.argv[2];

var stations = [];
fs.readdirSync("./station_modules").forEach(function(file){
	if(file.indexOf(".js") > 0){
		if(!stationToRun || file == stationToRun)
			stations.push(require("./station_modules/" + file));
	}
});


stations.forEach(function(station){
	station.fetch().then(function(data){
		console.log(station.code + ": " + data);
	});
});
