var davis = require("./super_stations/davis");


module.exports = {
	code: "tavernes",
	fetch: function(){
		return davis("meteovallplaya");
	}
}