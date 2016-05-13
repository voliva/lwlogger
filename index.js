var fs = require("fs");

var stationToRun = process.argv[2];

var fetchers = [];
fs.readdirSync("./station_modules").forEach(function(file){
	if(file.indexOf(".js") > 0){
		fetchers.push(require("./station_modules/" + file));
	}
});


fetchers.forEach(function(fetcher){
	fetcher.stations.forEach(function(station){
		if(stationToRun && station.code != stationToRun) return;

		fetcher.fetch(station.arg).then(function(data){
			console.log(station.code + ": " + data);
		});
	});
});

// Crazy fast idea: Locate+track where people go sailing in order to discover new places.
