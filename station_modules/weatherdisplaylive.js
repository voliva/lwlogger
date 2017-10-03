var lwutils = new (require("./super_stations/lwutils"))();
var Data = require("./../models/data");

var stations = [];
stations.push({id: 1, arg:{host: "www.cnps.cat", path:"/meteo_nova/clientraw.txt"}});
// stations.push({id: 4, arg: {host: "www.ballena-alegre.com", path: "/meteo/clientraw.txt"}});
stations.push({id: 5, arg: {host: "www.nauticescala.com", path: "/content/conf/clientraw.txt"}});
stations.push({id: 12, arg:{host: "www.clubnauticportdaro.cat", path:"/meteo/clientraw.txt"}});
stations.push({id: 14, arg:{host: "serveis.pinedasensefils.cat", path:"/clientraw.txt"}});
// Anemometre trencat stations.push({id: 23, arg: {host: "www.t33a.com", path: "/garraf/clientraw.txt"}});
stations.push({id: 34, arg: {host: "www.cnoropesa.com", path: "/wd/clientraw.txt"}});
stations.push({id: 39, arg: {host: "www.meteogandia.com", path: "/clientraw.txt"}});
stations.push({id: 41, arg: {host: "www.meteoxabia.com", path: "/clientraw.txt"}});
stations.push({id: 52, arg: {host: "www.meteodemallorca.com", path:"/eurotel/clientraw.txt"}});
stations.push({id: 53, arg: {host: "www.meteodemallorca.com", path:"/bahia_cala_millor/clientraw.txt"}});
stations.push({id: 54, arg: {host: "www.meteodemallorca.com", path:"/portopetro/clientraw.txt"}});
stations.push({id: 70, arg: {host: "www.meteopuertobanus.es", path: "/clientraw.txt"}});
stations.push({id: 80, arg: {host: "www.cngallineras.es", path: "/meteo/clientraw.txt"}});
stations.push({id: 85, arg: {host: "tiempo.fiochi.com", path: "/clientraw.txt"}});
stations.push(
	{
		id: 6,
		arg: {host: "www.cnestartit.es",
		path: "/webphp/clientraw.txt"
	},
	post: function(data){
		if(data == null) return null;
		if(data.temp == -10.1)
			data.temp = null;
		return data;
	}
});

function fetcher(args, timezone){
	var host = args.host;
	var path = args.path;

	return lwutils.getHTML(host, path).map(html => {
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

		return ret;
	});
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
