var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "marbella",
	fetch: function(){
		return wdl("www.meteopuertobanus.es", "/clientraw.txt");
	}
}