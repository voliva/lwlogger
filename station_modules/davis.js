var lwutils = new (require("./super_stations/lwutils"))();
var Q = require('q');
var Data = require("./../models/data");

var stations = [];
stations.push({code: "cma", arg: "cmaltafulla"});
stations.push({code: "canyelles", arg: "cncanyelles"});
stations.push({code: "getxo", arg: "rcmarsc"});
stations.push({code: "herradura", arg: "puntadelamona"});
stations.push({code: "malaga", arg: "tds"});
stations.push({code: "santapolacabo", arg: "runahue"});
stations.push({code: "sariera", arg: "pverhoeven"});
stations.push({code: "portsitges", arg: "portdesitges"});
stations.push({code: "stantonicalonge", arg: "calongeplatjatv"});
stations.push({code: "palamosplatja", arg: "ellado"});
stations.push({code: "tavernes", arg: "meteovallplaya"});
stations.push({code: "cmt", arg: "cmtorredembarra"});
stations.push({code: "valencia_marinareal", arg: "marinareal"});
stations.push({code: "cnvi", arg: "cnvitoria"});
stations.push({code: "altea", arg: "meteocnaltea"});
stations.push({code: "cascais", arg: "beachcamguincho"});
stations.push({code: "praiagrande", arg: "beachcampgrande"});
stations.push({code: "oportobares", arg: "parapenteferrol"});
stations.push({code: "zumaia", arg: "salvetzumaia1"});
stations.push({code: "torreviejasetsaas", arg: "setsaas"});
stations.push({code: "Cala Gamba", arg: "cncg"});
stations.push({code: "andratx", arg: "tinin30"});
stations.push({code: "Ibiza", arg: "ibizapilot"});
stations.push({code: "empuriabrava", arg: "trastitu", post: function(res){
  return res.then(function(data){
    data.dir = Math.floor(data.dir + 180) % 360;
    return data;
  });
}});

function fetcher(user, timezone){
	var deferred = new Q.defer();

	lwutils.getHTML("www.weatherlink.com", "/user/" + user + "/index.php?view=summary&headers=0&type=1").then(function(html){
		if(html.indexOf("Current Conditions") < 0) return deferred.reject("No current conditions");

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

		deferred.resolve(ret);
	});

	return deferred.promise;
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
