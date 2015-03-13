var wdl = require("./super_stations/weatherdisplaylive");

module.exports = {
	code: "santander",
	fetch: function(){
		return wdl("tiempo.fiochi.com", "/clientraw.txt");
	}
}