var lwutils = new (require("./super_stations/lwutils"))();
var Data = require("./../models/data");

module.exports = {
  stations: [{code:"17nudos", args:"yes"}],
  fetch: function fetcher(arg){
  	return lwutils.postHTML(
      "www.17nudos.com",
      "/update_me.php", {
        "Referer": "http://www.17nudos.com/index.php"
      }).then(function(html){

        var ret = new Data();
        ret.dateTime = new Date();

        var wind = new (lwutils.splitter)(html)
          .cropToStrEx("imagenes/viento.gif")
          .cropToStrEx("<br>")
          .getToStrEx("</td>")
          .getString()
          .trim();
        ret.wind = parseFloat(wind);

        var gust = new (lwutils.splitter)(html)
          .cropToStrEx("imagenes/racha.gif")
          .cropToStrEx("<br>")
          .getToStrEx("</td>")
          .getString()
          .trim();
        ret.gust = gust;

        var dir = new (lwutils.splitter)(html)
          .cropToStrEx("imagenes/direccion.gif")
          .cropToStrEx("<br>")
          .getToStrEx("</td>")
          .getString()
          .trim();
        ret.dir = lwutils.txtToDir(dir);

        return ret;
  	});
  }
}
