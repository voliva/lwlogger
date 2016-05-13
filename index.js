var fs = require("fs");
var stationsMonitor = require("./stationsMonitor");
var Q = require("q");
var esTz = require("timezone")(require("timezone/Europe/Madrid"));

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
				function appendNumber(val){
					str += (Math.round(val * 10) / 10) + "\t";
				}
				str += esTz(data.dateTime, "%H:%M", "Europe/Madrid") + "\t";
				appendNumber(data.temp);
				appendNumber(data.hidro);
				appendNumber(data.pressure);
				appendNumber(data.wind);
				appendNumber(data.gust);
				appendNumber(data.dir);
				appendNumber(data.rain);
				str += "\n";

				fs.appendFile("out.txt", str, function(err){
					if(err){
						console.log(err);
					}
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
