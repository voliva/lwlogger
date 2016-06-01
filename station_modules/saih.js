var lwutils = new (require("./super_stations/lwutils"))();
var Q = require('q');
var Data = require("./../models/data");

var stations = [];
// Good quality: 5-10 min
stations.push({code:"saih/ebro", arg: "EM01"});
stations.push({code:"saih/alloz", arg: "EM30"});
stations.push({code:"saih/lasotonera", arg: "EM38"});
stations.push({code:"saih/laestanca", arg: "EM19"});
stations.push({code:"saih/laloteta", arg: "E085"});

function format(date){
	var m = date.getMonth() + 1;
	var d = date.getDate();

	return date.getFullYear() + "-" +
		(m < 10 ? "0" + m : m) + "-" +
		(d < 10 ? "0" + d : d);
}

var loginPromise = null;
var postData = "tags%5B{i}%5D%5Btag%5D={tag}&tags%5B{i}%5D%5Bver%5D=S&tags%5B{i}%5D%5Bintervalo_15m%5D=1&tags%5B{i}%5D%5Bfecha_ini%5D={fecha_ini}-00-00&tags%5B{i}%5D%5Bfecha_fin%5D={fecha_fin}-00-00";

var now = new Date();
postData = postData.replace("{fecha_ini}", format(now));
now.setDate(now.getDate() + 1);
postData = postData.replace("{fecha_fin}", format(now));

function fetcher(id){
	if(!loginPromise){
		console.log("promise");
		loginPromise = lwutils.postHTML(
			"www.saihebro.com",
			"/saihebro/index.php?url=/usuarios/validarLogin",
			{
				"Content-Type": "application/x-www-form-urlencoded"
			},
			`data%5Blogin%5D%5Bnombreusuario%5D=${process.env.SAIH_USER}&data%5Blogin%5D%5Bpassword%5D=${process.env.SAIH_PASSWORD}&data%5Blogin%5D%5Brecordar%5D=1&data%5Blogin%5D%5Brecordar%5D=1`
		);
	}


	return loginPromise.then(function(html){
		return lwutils.getHTML(
	    "www.saihebro.com",
	    "/saihebro/index.php?url=/datos/ficha/estacion:" + id,
	    {
	      "Accept-Language": "es"
	    }
	  )
	}).then(function(html){
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
  });;

}

module.exports = {
  stations: stations,
  fetch: fetcher
}
