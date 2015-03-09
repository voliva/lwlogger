var davis = require("./super_stations/davis");


module.exports = {
	code: "santapolacabo",
	fetch: function(){
		return davis("runahue");
	}
}