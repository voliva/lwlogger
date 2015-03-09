var davis = require("./super_stations/davis");


module.exports = {
	code: "cma",
	fetch: function(){
		return davis("cmaltafulla");
	}
}