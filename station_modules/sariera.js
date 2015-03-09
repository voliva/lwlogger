var davis = require("./super_stations/davis");


module.exports = {
	code: "sariera",
	fetch: function(){
		return davis("pverhoeven");
	}
}