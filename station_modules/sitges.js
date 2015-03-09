var davis = require("./super_stations/davis");


module.exports = {
	code: "portsitges",
	fetch: function(){
		return davis("portdesitges");
	}
}