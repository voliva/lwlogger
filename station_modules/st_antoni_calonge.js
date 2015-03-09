var davis = require("./super_stations/davis");


module.exports = {
	code: "stantonicalonge",
	fetch: function(){
		return davis("calongeplatjatv");
	}
}