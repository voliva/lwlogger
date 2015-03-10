var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "cnga",
	fetch: function(){
		return wdl("www.cngallineras.es", "/meteo/clientraw.txt");
	}
}