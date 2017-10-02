// http://servidor.meteotek3000.com/estaciones/mkiii/jstags.php?username=tagsport&passwd=1772&id=4

var lwutils = new (require("./super_stations/lwutils"))();
var Data = require("./../models/data");
var vm = require("vm");

module.exports = {
  stations: [{code:"cmv", args:"yes"}],
  fetch: function fetcher(arg){
  	return lwutils.getHTML("servidor.meteotek3000.com", "/estaciones/mkiii/jstags.php?username=tagsport&passwd=1772&id=4").then(function(js){
      js =
        "var result = {};" +
        js.replace(/var /g, "result.")
          .replace(/,/g, ".") +
        "result;";

      var result = vm.runInThisContext(js);

  		var ret = new Data();

  		var time = result.hora.split(":");
  		var date = result.fecha.split("/");

  		ret.dateTime = lwutils.getDate(date[2], date[1], date[0], time[0], time[1]);
      ret.temp = parseFloat(result.temp);
      ret.hidro = parseFloat(result.hum);
      ret.pressure = parseFloat(result.pres);
      ret.dir = lwutils.txtToDir(result.dirvelviento);
      ret.wind = lwutils.kmhToKnots(parseFloat(result.velviento));
      ret.gust = lwutils.kmhToKnots(parseFloat(result.hivelviento));
      ret.rain = parseFloat(result.lluviadia);

  		return ret;
  	});
  }
}
