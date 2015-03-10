var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "cng",
	fetch: function(){
		return wdl("www.t33a.com", "/garraf/clientraw.txt");
	}
}