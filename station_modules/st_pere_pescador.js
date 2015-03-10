var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "stperepescador",
	fetch: function(){
		return wdl("www.ballena-alegre.com", "/meteo/clientraw.txt");
	}
}