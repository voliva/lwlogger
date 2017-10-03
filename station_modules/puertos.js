var lwutils = new (require("./super_stations/lwutils"))();
var Data = require("./../models/data");

var stations = [];
// Good quality: 5-10 min
stations.push({id:29, arg: 3756});
stations.push({id:49, arg: 3860});
stations.push({id:51, arg: 3853});
stations.push({id:56, arg: 3851});
stations.push({id:60, arg: 3856});
stations.push({id:62, arg: 3855});
stations.push({id:63, arg: 3547});
stations.push({id:64, arg: 4581});
stations.push({id:66, arg: 3545});
stations.push({id:67, arg: 3548});
stations.push({id:71, arg: 4396});
stations.push({id:72, arg: 3541});
stations.push({id:73, arg: 4397});
stations.push({id:74, arg: 4391});
stations.push({id:75, arg: 4394});
stations.push({id:76, arg: 4395}); // Parada
stations.push({id:77, arg: 4392});
stations.push({id:78, arg: 4393});
stations.push({id:79, arg: 4398});
stations.push({id:93, arg: 3221});
stations.push({id:91, arg: 3215});
stations.push({id:87, arg: 3108});
stations.push({id:100, arg: 3470});
stations.push({id:101, arg: 3469});
stations.push({id:102, arg: 3450});

// Bad quality: 1h
stations.push({id:8, arg: 2798});
stations.push({id:32, arg: 2720});
stations.push({id:35, arg: 2630});
stations.push({id:50, arg: 2838});
stations.push({id:58, arg: 2820});
stations.push({id:47, arg: 2610});
stations.push({id:65, arg: 2548});
// Averiada stations.push({id:"puertos/carneroBoya", arg: 1504});
stations.push({id:81, arg: 2342});
stations.push({id:92, arg: 1239});
stations.push({id:90, arg: 2244});
stations.push({id:88, arg: 2242});
stations.push({id:86, arg: 2150});
stations.push({id:84, arg: 2136});
stations.push({id:103, arg: 2442});
stations.push({id:104, arg: 2446});


function fetcher(id, timezone){
	return lwutils.postHTML(
    "portus.puertos.es",
    "/Portus_RT/portusgwt/rpc", {
      "Content-Type": "text/x-gwt-rpc; charset=UTF-8",
      "X-GWT-Module-Base": "http://portus.puertos.es/Portus_RT/portusgwt/",
      "X-GWT-Permutation": "111ADDA4C45CBCEFFA5F277B516481A5"
    },
    "7|0|5|http://portus.puertos.es/Portus_RT/portusgwt/|4E7E7D41B0B2B89535613848D893F712|es.puertos.portus.main.client.service.PortusService|requestLastData|I|1|2|3|4|1|5|" + id + "|"
  ).map(html => {
    var array = new (lwutils.splitter)(html)
      .cropToStrEx("[")
      .cropToStrEx("[\"")
      .getToStrEx("\"]")
      .getString()
      .split("\",\"");

		/* Logic
		1. datetime format is NN-NN-NNNN NN:NN:NN
		2. if it contains a mb it's pressure
		3. if it contains ºC it's temperature, if it's behind V
		4. the first m/s is wind
		5. the first º after wind is dir
		4. next m/s is gust
		*/

    var ret = new Data();
		var timeRegex = /^([0-9]{2})\-([0-9]{2})\-([0-9]{4}) ([0-9]{2}):([0-9]{2}):([0-9]{2})$/;
		var vFound = false,
			windFound = false;
		array.forEach(function(v){
			if(!ret.time){
				var match = timeRegex.exec(v);
				if(match){
					ret.dateTime = new Date(
						match[3] + "-" +
						match[2] + "-" +
						match[1] + "T" +
						match[4] + ":" +
						match[5] + ":" +
						match[6] + "Z"
					);
					return;
				}
			}

			if(v.indexOf("(mb)") >= 0){
				ret.pressure = parseFloat(v);
				return;
			}
			if(v.indexOf("(V)") >= 0){
				vFound = true;
				return;
			}
			if(!ret.temp && v.indexOf("(ºC)") >= 0){
				ret.temp = parseFloat(v);
				return;
			}

			if(v.indexOf("(m/s)") >= 0){
				if(!windFound){
					ret.wind = lwutils.mpsToKnots(parseFloat(v));
					windFound = true;
					return;
				}else if(!ret.gust){
					ret.gust = lwutils.mpsToKnots(parseFloat(v));
				}
			}

			if(!ret.dir && windFound && v.indexOf("(º)") >= 0){
				ret.dir = parseFloat(v);
			}
		});
		if(!ret.dateTime) ret.dateTime = new Date();

		return ret;
	});
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
