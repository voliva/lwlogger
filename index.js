var fs = require("fs");
var stationsMonitor = require("./stationsMonitor");
var Q = require("q");
var mkdirp = require("mkdirp");
var esTz = require("timezone")(require("timezone/Europe/Madrid"));

var FILE_ROOT = "../www/dades"

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

		var p = fetcher.fetch(station.arg);
		if(station.post){
			p = station.post(p);
		}
		p.then(function(data){
			if(stationToRun){
				// console.log(fetcher);
				console.log(data);
				return;
			}
			try {
				var res = stationsMonitor.check(station.code, data);
				if(!res) return;

				var strs = [];
				var time = data.dateTime;
				var folder = FILE_ROOT + "/" + station.code;
				var filename = folder + "/" + esTz(time, "%Y_%m_%d", "Europe/Madrid") + ".dat";
				for(var i=0; i<res; i++){
					var str = "";
					function appendNumber(val){
						if(typeof val == "undefined") return str += "-\t";
						str += (Math.round(val * 10) / 10) + "\t";
					}
					str += esTz(time, "%H:%M", "Europe/Madrid") + "\t";
					appendNumber(data.temp);
					appendNumber(data.hidro);
					appendNumber(data.pressure);
					appendNumber(data.wind);
					appendNumber(data.gust);
					appendNumber(data.dir);
					appendNumber(data.rain);
					str += "\n";

					strs.unshift(str);
					time = esTz(time, "-5 minutes");
				}

				mkdirp(folder, function(err){
					if(err){
						console.log(err);
						return;
					}

					strs.forEach(function(str){
						fs.appendFile(filename, str, function(err){
							if(err){
								console.log(err);
							}
						});
					});
				});
			}catch(ex){
				console.log(station.code, err, data);
				console.log(err.stack);
			}
		}, function(err){
			console.log(station.code, err);
			console.log(err.stack);
		});
		promises.push(p);
	});
});

if(!stationToRun){
	Q.all(promises).then(function(){
		stationsMonitor.save();
	}, function(err){
		stationsMonitor.save();
	});
}

// Crazy fast idea: Locate+track where people go sailing in order to discover new places.
