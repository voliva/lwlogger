var davis = require("./super_stations/davis");


module.exports = {
	code: "herradura",
	fetch: function(){
		return davis("puntadelamona");
	}
}