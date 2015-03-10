var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "cnle",
	fetch: function(){
		return wdl("www.nauticescala.com", "/content/conf/clientraw.txt");
	}
}