var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "xabia",
	fetch: function(){
		return wdl("www.meteoxabia.com", "/clientraw.txt");
	}
}