var lwutils = new (require("./super_stations/lwutils"))();
var Q = require('q');
var Data = require("./../models/data");

var stations = [];
stations.push({code: "mctest", arg: {region: "ESCAT", code: "ESCAT0800000008915A"}});

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
    while(res = captureRegexp.exec(html)){
      console.log(i, res[1]);
      i++;
    }

    /* Now it's col 10
    var windCaptureRegexp = />([0-9]+)&nbsp;([NSEW]+)</g;
    res = windCaptureRegexp.exec(html);
    if(res){
      console.log("Wind", res[1], "Dir", res[2]);
    }*/
  });
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
