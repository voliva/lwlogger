var lwutils = new (require("./super_stations/lwutils"))();
var Data = require("./../models/data");

var stations = [];
stations.push({id: 33, arg: {region: "ESCAT", code: "ESCAT4300000043540A"}});
stations.push({id: 30, arg: {region: "ESCAT", code: "ESCAT4300000043892C"}});
stations.push({id: 31, arg: {region: "ESCAT", code: "ESCAT4300000043890A"}});
stations.push({id: 26, arg: {region: "ESCAT", code: "ESCAT0800000008880D"}}); // Nativa http://meteo.ea3hkb.com/
stations.push({id: 25, arg: {region: "ESCAT", code: "ESCAT0800000008800F"}}); // Nativa http://www.meteovilanova.info/Current_Vantage.htm
stations.push({id: 22, arg: {region: "ESCAT", code: "ESCAT0800000008860B"}});
stations.push({id: 20, arg: {region: "ESCAT", code: "ESCAT0800000008003F"}}); // Nativa http://meteo.cmima.csic.es/davis/Current_Vantage_Pro_Plus.html
stations.push({id: 17, arg: {region: "ESCAT", code: "ESCAT0800000008330B"}}); // Nativa http://www.meteopremia.com/port/
stations.push({id: 18, arg: {region: "ESCAT", code: "ESCAT0800000008330E"}}); // Nativa http://cbc117.zapto.org/
stations.push({id: 16, arg: {region: "ESCAT", code: "ESCAT0800000008301F"}});
stations.push({id: 15, arg: {region: "ESCAT", code: "ESCAT0800000008370A"}});
stations.push({id: 48, arg: {region: "ESIBA", code: "ESIBA0700000307760A"}});
stations.push({id: 59, arg: {region: "ESIBA", code: "ESIBA0700000207840A"}})

// premia cabrera mataro canet calella
// TODO Awekas: (premià http://www.awekas.at/es/instrument.php?id=12238)


var MCData = {};
function getMCData(region){
  if(MCData[region]) return MCData[region];

  // Assuming 1 thread!
  MCData[region] = new Promise((resolve, reject) => {
    lwutils.getHTML("www.meteoclimatic.net", "/mapinfo/" + region, {
      "Accept-Language": "ca-ES"
    }, true)
    .then(function(html){
      resolve(new (lwutils.splitter)(html)
        .cropToStrEx("<!-- Calendarii -->")
        .getToStrEx(`<map name="estacions" id="estacions">`)
        .getString());
    }, (err) => {
      reject(err);
    });
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
