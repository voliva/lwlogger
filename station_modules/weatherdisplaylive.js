var lwutils = new (require("./super_stations/lwutils"))();
var Q = require('q');
var Data = require("./../models/data");

var stations = [];
stations.push({code: "cnga", arg: {host: "www.cngallineras.es", path: "/meteo/clientraw.txt"}});
stations.push({code: "rcng", arg: {host: "www.meteogandia.com", path: "/clientraw.txt"}});
// Anemometre trencat stations.push({code: "cng", arg: {host: "www.t33a.com", path: "/garraf/clientraw.txt"}});
stations.push({code: "cnle", arg: {host: "www.nauticescala.com", path: "/content/conf/clientraw.txt"}});
stations.push({code: "marbella", arg: {host: "www.meteopuertobanus.es", path: "/clientraw.txt"}});
stations.push({code: "cno", arg: {host: "www.cnoropesa.com", path: "/wd/clientraw.txt"}});
stations.push({code: "santander", arg: {host: "tiempo.fiochi.com", path: "/clientraw.txt"}});
stations.push({code: "stperepescador", arg: {host: "www.ballena-alegre.com", path: "/meteo/clientraw.txt"}});
stations.push({code: "xabia", arg: {host: "www.meteoxabia.com", path: "/clientraw.txt"}});
stations.push({code: "mallorca/portopetro", arg: {host: "www.meteodemallorca.com", path:"/portopetro/clientraw.txt"}});
stations.push({code: "mallorca/calamillor", arg: {host: "www.meteodemallorca.com", path:"/bahia_cala_millor/clientraw.txt"}});
stations.push({code: "mallorca/eurotel", arg: {host: "www.meteodemallorca.com", path:"/eurotel/clientraw.txt"}});
stations.push({code: "cnps", arg:{host: "www.cnps.cat", path:"/meteo_nova/clientraw.txt"}});
stations.push({code: "cnpa", arg:{host: "www.clubnauticportdaro.cat", path:"/meteo/clientraw.txt"}});
stations.push({code: "pineda", arg:{host: "serveis.pinedasensefils.cat", path:"/clientraw.txt"}});
stations.push({code: "cnga", arg:{host: "www.cngallineras.es", path:"/meteo/clientraw.txt"}});
stations.push(
	{
		code: "cne",
		arg: {host: "www.cnestartit.es",
		path: "/webphp/clientraw.txt"
	},
	post: function(res){
		return res.then(function(data){
			if(data.temp == -10.1)
				data.temp = null;
			return data;
		});
	}
});



function fetcher(args, timezone){
	var host = args.host;
	var path = args.path;

	var deferred = new Q.defer();

	lwutils.getHTML(host, path).then(function(html){
		var arr = html.split(" ");
		if(arr.length < 142) return null;

		var ret = new Data();
		ret.temp = parseFloat(arr[4]);
		ret.hidro = parseFloat(arr[5]);
		ret.pressure = parseFloat(arr[6]);
		ret.wind = parseFloat(arr[1]);
		ret.gust = parseFloat(arr[140]);
		ret.dir = parseFloat(arr[3]);
		ret.rain = parseFloat(arr[7]);

		ret.dateTime = lwutils.getDate(arr[141], arr[36], arr[35], arr[29], arr[30], timezone);

		// Aquestes estacions solen donar gust < wind, fent que el grafic quedi malament
		// Encara que no sigui del tot correcte, manipularé les dades en aquest cas, perque no quedi lleig en el gràfic.
		if(ret.gust < ret.wind) ret.gust = ret.wind;


		deferred.resolve(ret);
	});

	return deferred.promise;
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
