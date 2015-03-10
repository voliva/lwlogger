var wdl = require("./super_stations/weatherdisplaylive");


module.exports = {
	code: "cno",
	fetch: function(){
		return wdl("www.cnoropesa.com", "/wd/clientraw.txt");
	}
}