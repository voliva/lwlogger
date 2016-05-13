var lwutils = new (require("./super_stations/lwutils"))();
var Q = require('q');
var Data = require("./../models/data");

var stations = [];
// Good quality: 5-10 min
stations.push({code:"puertos/tarragona", arg: 3756});
stations.push({code:"puertos/maho", arg: 3860});
stations.push({code:"puertos/alcudia", arg: 3853});
stations.push({code:"puertos/palmamall", arg: 3851});
stations.push({code:"puertos/ibiza", arg: 3856});
stations.push({code:"puertos/formentera", arg: 3855});
stations.push({code:"puertos/carboneras", arg: 3547});
stations.push({code:"puertos/roldan", arg: 4581});
stations.push({code:"puertos/almeria", arg: 3545});
stations.push({code:"puertos/almeria2", arg: 3548});
stations.push({code:"puertos/campamento", arg: 4396});
stations.push({code:"puertos/algeciras", arg: 3541});
stations.push({code:"puertos/endesa", arg: 4397});
stations.push({code:"puertos/abrigo", arg: 4391});
stations.push({code:"puertos/exentoN", arg: 4394}); // Parada
stations.push({code:"puertos/exentoS", arg: 4395}); // Parada
stations.push({code:"puertos/carnero", arg: 4392});
stations.push({code:"puertos/tarifa", arg: 4393});
stations.push({code:"puertos/tarifaDique", arg: 4398});
stations.push({code:"puertos/vigo2", arg: 3221});
stations.push({code:"puertos/ferrol", arg: 3215});
stations.push({code:"puertos/gijon2", arg: 3108});
stations.push({code:"puertos/lanzarote", arg: 3470});
stations.push({code:"puertos/fuerteventura", arg: 3469});
stations.push({code:"puertos/laspalmas", arg: 3450});

// Bad quality: 1h
stations.push({code:"puertos/begur", arg: 2798});
stations.push({code:"puertos/tarrboya", arg: 2720});
stations.push({code:"puertos/valencia", arg: 2630});
stations.push({code:"puertos/mahoboya", arg: 2838});
stations.push({code:"puertos/dragonera", arg: 2820});
stations.push({code:"puertos/palos", arg: 2610});
stations.push({code:"puertos/gata", arg: 2548});
stations.push({code:"puertos/carneroBoya", arg: 1504});
stations.push({code:"puertos/cadizBoya", arg: 2342});
stations.push({code:"puertos/langosteira", arg: 1239});
stations.push({code:"puertos/bares", arg: 2244});
stations.push({code:"puertos/penas", arg: 2242});
stations.push({code:"puertos/santander", arg: 2150});
stations.push({code:"puertos/bilbao", arg: 2136});
stations.push({code:"puertos/grancanaria", arg: 2442});
stations.push({code:"puertos/tenerifeS", arg: 2446});


function fetcher(id, timezone){
	var deferred = new Q.defer();

	return lwutils.postHTML(
    "portus.puertos.es",
    "/Portus_RT/portusgwt/rpc", {
      "Content-Type": "text/x-gwt-rpc; charset=UTF-8",
      "X-GWT-Module-Base": "http://portus.puertos.es/Portus_RT/portusgwt/",
      "X-GWT-Permutation": "111ADDA4C45CBCEFFA5F277B516481A5"
    },
    "7|0|5|http://portus.puertos.es/Portus_RT/portusgwt/|4E7E7D41B0B2B89535613848D893F712|es.puertos.portus.main.client.service.PortusService|requestLastData|I|1|2|3|4|1|5|" + id + "|"
  ).then(function(html){
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
	}, function(err){
    console.log("Err", err);
  });
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
