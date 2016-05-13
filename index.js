var fs = require("fs");
var stationsMonitor = require("./stationsMonitor");
var Q = require("q")

var stationToRun = process.argv[2];

var fetchers = [];
fs.readdirSync("./station_modules").forEach(function(file){
	if(file.indexOf(".js") > 0){
		fetchers.push(require("./station_modules/" + file));
	}
});

var promises = []

fetchers.forEach(function(fetcher){
	fetcher.stations.forEach(function(station){
		if(stationToRun && station.code != stationToRun) return;

		var p = fetcher.fetch(station.arg).then(function(data){
			try {
				var res = stationsMonitor.check(station.code, data);
				if(!res) return;
				if(res === true) res = 1;

				var str = "";
				str += "00:00" + "\t";
				str += res.temp + "\t";
				str += res.hidro + "\t";
				str += res.pressure + "\t";
				str += res.wind + "\t";
				str += res.gust + "\t";
				str += res.dir + "\t";
				str += res.rain;

				fs.appendFile("out.txt", str, function(err){
					console.log("Done?", err);
				});
			}catch(ex){
				console.log(ex);
			}
		});
		promises.push(p);
	});
});

Q.all(promises).then(function(){
	stationsMonitor.save();
});

// Crazy fast idea: Locate+track where people go sailing in order to discover new places.
