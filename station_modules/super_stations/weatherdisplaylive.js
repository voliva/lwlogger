var lwutils = new (require("./lwutils"))();
var Q = require('q');
var Data = require("./../../models/data");

module.exports = function(host, path, timezone){
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