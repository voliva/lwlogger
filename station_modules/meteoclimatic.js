var lwutils = new (require("./super_stations/lwutils"))();
var Q = require('q');
var Data = require("./../models/data");

var stations = [];
stations.push({code: "stcarlesrapita", arg: {region: "ESCAT", code: "ESCAT4300000043540A"}});

var MCData = {};
function getMCData(region){
  var deferred = new Q.defer();

  // Assuming 1 thread!
  if(MCData[region]) return MCData;
  MCData[region] = deferred.promise;

	lwutils.getHTML("www.meteoclimatic.net", "/mapinfo/" + region, {
    "Accept-Language": "ca-ES"
  }).then(function(html){
    deferred.resolve(new (lwutils.splitter)(html)
      .cropToStrEx("<!-- Calendarii -->")
      .getToStrEx("amb segell Meteoclimatic de qualitat destacada")
      .getString());
	});

	return MCData[region];
}

function fetcher(args, timezone){
  var region = args.region;
  var code = args.code;

  return getMCData(region).then(function(html){
    html = (new (lwutils.splitter)(html))
      .cropToStrEx("/perfil/" + code)
      .getToStrEx("</tr>")
      .getString();

    if(html.indexOf("background: #FFADAD;") >= 0) return null; // >1.5h lag
    if(html.indexOf("background: #FFFFB9;") >= 0) return null; // >20min lag

    var captureRegexp = /<td.*?>(.*?)<\/td/g;
    var i = 0;
    var res;
    var ret = new Data();
    ret.dateTime = new Date();
    while(res = captureRegexp.exec(html)){
      if(!res || res.length < 2) break;

      res = res[1].replace(",",".");
      var resf = parseFloat(res);
      if(i == 1 && resf){
        ret.temp = resf;
      }else if(i == 4 && resf){
        ret.hidro = resf;
      }else if(i == 7 && resf){
        ret.pressure = resf;
      }else if(i == 10){
        res = res.split("&nbsp;");
        if(res.length > 0){
          ret.dir = lwutils.txtToDir(res[1], 0);
          ret.wind = lwutils.kmhToKnots(parseFloat(res[0].replace(",", ".")));
        }
      }

      i++;
    }

    return ret;
  });
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
