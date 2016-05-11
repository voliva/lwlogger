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

    var ret = new Data();
    // 3 time
    if(array.length == 40){
      ret.pressure = parseFloat(array[4]);
      ret.temp = parseFloat(array[5]);
      ret.wind = lwutils.mpsToKnots(parseFloat(array[15]));
      ret.dir = parseFloat(array[16]);
      return ret;
    }else if(array.length == 15){
      /* 0 2 12 1
      "java.util.HashMap/1797211028"
      "java.lang.Integer/3438268394"
      "java.lang.String/2004016611
      51 52 */
      ret.pressure = parseFloat(array[6]);
      ret.wind = lwutils.mpsToKnots(parseFloat(array[9]));
      ret.gust = lwutils.mpsToKnots(parseFloat(array[10]))
      ret.dir = parseFloat(array[11]);
      return ret;
    }else{
      console.log("Unknown length: " + array.length, id);
      return null;
    }
	}, function(err){
    console.log("Err", err);
  });
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
