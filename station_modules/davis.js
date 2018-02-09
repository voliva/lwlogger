var lwutils = new (require("./super_stations/lwutils"))();
var Data = require("./../models/data");

var stations = [];
stations.push({id: 28, arg: "cmaltafulla"});
stations.push({id: 13, arg: "cncanyelles"});
stations.push({id: 83, arg: "rcmarsc"});
stations.push({id: 68, arg: "puntadelamona"});
stations.push({id: 69, arg: "tds"});
stations.push({id: 43, arg: "runahue"});
stations.push({id: 7, arg: "pverhoeven"});
stations.push({id: 24, arg: "portdesitges"});
stations.push({id: 11, arg: "calongeplatjatv"});
stations.push({id: 10, arg: "ellado"});
stations.push({id: 38, arg: "meteovallplaya"});
stations.push({id: 27, arg: "cmtorredembarra"});
stations.push({id: 36, arg: "marinareal"});
stations.push({id: 95, arg: "cnvitoria"});
stations.push({id: 42, arg: "meteocnaltea"});
stations.push({id: 107, arg: "beachcamguincho"});
stations.push({id: 106, arg: "beachcampgrande"});
stations.push({id: 89, arg: "parapenteferrol"});
stations.push({id: 82, arg: "salvetzumaia1"});
stations.push({id: 44, arg: "setsaas"});
stations.push({id: 55, arg: "cncg"});
stations.push({id: 57, arg: "tinin30"});
stations.push({id: 61, arg: "ibizapilot"});
stations.push({id: 3, arg: "trastitu", post: function(data){
	data.dir = Math.floor(data.dir + 180) % 360;
}});
stations.push({id: 12, arg:"cnportdaro"});

function fetcher(user, timezone){
	return lwutils.getHTML("www.weatherlink.com", "/user/" + user + "/index.php?view=summary&headers=0&type=1")
	.map(html => {
		if(html.indexOf("Current Conditions") < 0) return null;

		var ret = new Data();

		var time = new (lwutils.splitter)(html)
			.cropToStrEx("summary_timestamp")
			.cropToStrEx("Current Conditions as of ")
			.getToStrEx(" ")
			.getString()
			.split(":");

		var date = new (lwutils.splitter)(html)
			.cropToStrEx("summary_timestamp")
			.cropToStrEx("Current Conditions as of ")
			.cropToStrEx(", ") // Sunday, March 8, 2015
			.getToStrEx("</td>")
			.getString()
			.split(" ");
		date[0] = lwutils.nomMesEngToNum(date[0]);
		date[1] = date[1].replace(",", "");

		// TODO hi ha hagut un error a les 00:09-00:12...

		ret.dateTime = lwutils.getDate(date[2], date[0], date[1], time[0], time[1], timezone);

		var temp = new (lwutils.splitter)(html)
			.cropToStrEx("Outside Temp")
			.cropToStrEx("summary_data")
			.cropToStrEx(">")
			.getToStrEx(" C")
			.getString();
		if(lwutils.isNumber(temp)){
			ret.temp = parseFloat(temp);
		}

		var humidity = new (lwutils.splitter)(html)
			.cropToStrEx("Outside Humidity")
			.cropToStrEx("summary_data")
			.cropToStrEx(">")
			.getToStrEx("%")
			.getString();
		if(lwutils.isNumber(humidity)){
			ret.hidro = parseFloat(humidity);
		}

		var barometer = new (lwutils.splitter)(html)
			.cropToStrEx("Barometer")
			.cropToStrEx("summary_data")
			.cropToStrEx(">")
			.getToStrEx("mb")
			.getString();
		if(lwutils.isNumber(barometer)){
			ret.pressure = parseFloat(barometer);
		}

		var dir = new (lwutils.splitter)(html)
			.cropToStrEx("Wind Direction")
			.cropToStrEx("summary_data")
			.cropToStrEx(">")
			.cropToStrEx(";")
			.getToStrEx("&deg;")
			.getString();
		if(lwutils.isNumber(dir)){
			ret.dir = parseFloat(dir);
		}

		var wind_sp = new (lwutils.splitter)(html)
			.cropToStrEx("Wind Speed")
			.cropToStrEx("summary_data")
			.cropToStrEx(">")
			.getToStrEx("</td>");
		if(wind_sp.getString() == "Calm")
			ret.wind = 0;
		else{
			var wind = wind_sp.getToStrEx(" km/h").getString();

			if(lwutils.isNumber(wind)){
				ret.wind = lwutils.kmhToKnots(wind);
			}
		}

		var gust_sp = new (lwutils.splitter)(html)
			.cropToStrEx("Wind Gust Speed")
			.cropToStrEx("summary_data")
			.cropToStrEx("summary_data")
			.cropToStrEx(">")
			.getToStrEx("</td>");
		if(gust_sp.getString() == "Calm")
			ret.gust = 0;
		else{
			var gust = gust_sp.getToStrEx(" km/h").getString();

			if(lwutils.isNumber(gust)){
				ret.gust = lwutils.kmhToKnots(gust);
			}
		}

		var rain = new (lwutils.splitter)(html)
			.cropToStrEx("class=\"summary_data\">Rain")
			.cropToStrEx("summary_data")
			.cropToStrEx(">")
			.getToStrEx("mm/Hour")
			.getString();
		if(lwutils.isNumber(rain)){
			ret.rain = parseFloat(rain);
		}

		return ret;
	});
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
