var davis = require("./super_stations/davis");


module.exports = {
	code: "ampolla",
	fetch: function(){
		return davis("meteoampolla");
	}
}