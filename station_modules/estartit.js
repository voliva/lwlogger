var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "cne",
	fetch: function(){
		return wdl("www.cnestartit.es", "/webphp/clientraw.txt");
	}
}