var davis = require("./super_stations/davis");


module.exports = {
	code: "cnvi",
	fetch: function(){
		return davis("cnvitoria");
	}
}