var davis = require("./super_stations/davis");


module.exports = {
	code: "valencia_marinareal",
	fetch: function(){
		return davis("marinareal");
	}
}