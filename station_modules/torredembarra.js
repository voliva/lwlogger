var davis = require("./super_stations/davis");


module.exports = {
	code: "cmt",
	fetch: function(){
		return davis("cmtorredembarra");
	}
}