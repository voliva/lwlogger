var davis = require("./super_stations/davis");


module.exports = {
	code: "getxo",
	fetch: function(){
		return davis("rcmarsc");
	}
}