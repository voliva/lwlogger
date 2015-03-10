var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "rcng",
	fetch: function(){
		return wdl("www.meteogandia.com", "/clientraw.txt");
	}
}