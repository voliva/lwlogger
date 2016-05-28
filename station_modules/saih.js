var lwutils = new (require("./super_stations/lwutils"))();
var Q = require('q');
var Data = require("./../models/data");

var stations = [];
// Good quality: 5-10 min
stations.push({code:"saih/ebro", arg: "EM01"});
stations.push({code:"saih/alloz", arg: "EM30"});
stations.push({code:"saih/lasotonera", arg: "EM38"});
stations.push({code:"saih/laestanca", arg: "EM19"});


function fetcher(id){
	return lwutils.getHTML(
    "www.saihebro.com",
    "/saihebro/index.php?url=/datos/ficha/estacion:" + id,
    {
      "Accept-Language": "es"
    }
  ).then(function(html){
    html = new (lwutils.splitter)(html)
      .cropToStrEx("Datos anal")
      .getToStrEx("</table>")
      .getString();

    var ret = new Data();

    var wind = new (lwutils.splitter)(html)
      .cropToStrEx("VELOCIDAD VIENTO")
      .getToStrEx("</tr>")
      .getString()
      .replace(/\r?\n|\r/g, "");

    var match = />([0-9\/ :]+)<\/td.+?celdac\">([0-9,]+)<\/td/.exec(wind);
    if(match){
      var timeRegex = /^([0-9]{2})\/([0-9]{2})\/([0-9]{4}) ([0-9]{2}):([0-9]{2})$/;
      var timeMatch = timeRegex.exec(match[1]);
      if(timeMatch){
        ret.dateTime = new Date(
          timeMatch[3] + "-" +
          timeMatch[2] + "-" +
          timeMatch[1] + "T" +
          timeMatch[4] + ":" +
          timeMatch[5] + ":00Z"
        );
      }

      ret.wind = lwutils.mpsToKnots(parseFloat(match[2].replace(",", ".")));
    }

    var temp = new (lwutils.splitter)(html)
      .cropToStrEx("TEMPERATURA AMB.")
      .getToStrEx("</tr>")
      .getString()
      .replace(/\r?\n|\r/g, "");
    match = />([0-9,]+)<\/td/.exec(temp);
    if(match){
      ret.temp = parseFloat(match[1].replace(",", "."));
    }

    var gust = new (lwutils.splitter)(html)
      .cropToStrEx("VELOC.RACHA")
      .getToStrEx("</tr>")
      .getString()
      .replace(/\r?\n|\r/g, "");
    match = />([0-9,]+)<\/td/.exec(gust);
    if(match){
      ret.gust = lwutils.mpsToKnots(parseFloat(match[1].replace(",", ".")));
    }

    var dir = new (lwutils.splitter)(html)
      .cropToStrEx("DIRECCI")
      .getToStrEx("</tr>")
      .getString()
      .replace(/\r?\n|\r/g, "");
    match = />([0-9,]+)<\/td/.exec(dir);
    if(match){
      ret.dir = parseFloat(match[1].replace(",", "."));
    }

    var rain = new (lwutils.splitter)(html)
      .cropToStrEx("PRECIP. HORARIA")
      .getToStrEx("</tr>")
      .getString()
      .replace(/\r?\n|\r/g, "");
    match = />([0-9,]+)<\/td/.exec(rain);
    if(match){
      ret.rain = parseFloat(match[1].replace(",", "."));
    }

    var hum = new (lwutils.splitter)(html)
      .cropToStrEx("HUMEDAD RELAT")
      .getToStrEx("</tr>")
      .getString()
      .replace(/\r?\n|\r/g, "");
    match = />([0-9,]+)<\/td/.exec(hum);
    if(match){
      ret.hidro = parseFloat(match[1].replace(",", "."));
    }

    var pressure = new (lwutils.splitter)(html)
      .cropToStrEx("PRESION NIVEL DEL MAR")
      .getToStrEx("</tr>")
      .getString()
      .replace(/\r?\n|\r/g, "");
    match = />([0-9,.]+)<\/td/.exec(pressure);
    if(match){
      ret.pressure = parseFloat(
				match[1]
					.replace(".", "")
					.replace(",", ".")
			);
    }

    return ret;
	}, function(err){
    console.log("Err", err);
  });
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
