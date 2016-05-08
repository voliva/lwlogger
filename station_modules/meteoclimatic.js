var lwutils = new (require("./super_stations/lwutils"))();
var Q = require('q');
var Data = require("./../models/data");

var stations = [];
stations.push({code: "mctest", arg: {region: "ESCAT", code: "Whatever"}});

var MCData = {};
function getMCData(region){
  var deferred = new Q.defer();

  // Assuming 1 thread!
  if(MCData[region]) return MCData;
  MCData[region] = deferred.promise;

	lwutils.getHTML("www.meteoclimatic.net", "/mapinfo/" + region).then(function(html){
    deferred.resolve(new (lwutils.splitter)(html)
      .cropToStrEx("<!-- Calendarii -->")
      .getToStrEx("amb segell Meteoclimatic de qualitat destacada"));
	});

	return MCData[region];
}

function fetcher(args, timezone){
  var region = args.region;
  var code = args.code;
  console.log("Fetch");

  return getMCData(region).then(function(splitter){
    console.log(splitter.getString());
  });
}

module.exports = {
  stations: stations,
  fetch: fetcher
}
