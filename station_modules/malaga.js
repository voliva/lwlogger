var davis = require("./super_stations/davis");


module.exports = {
	code: "malaga",
	fetch: function(){
		return davis("tds");
	}
}