var davis = require("./super_stations/davis");


module.exports = {
	code: "canyelles",
	fetch: function(){
		return davis("cncanyelles");
	}
}