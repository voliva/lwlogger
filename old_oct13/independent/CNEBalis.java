/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.independent;

import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public class CNEBalis implements StationFetcher {

    @Override
    public Dada fetch() {
        String html = Globals.getHTML("http://www.cnelbalis.com/meteo_estaciones.asp");
        if(html == null) return null;
        
        Globals.Splitter sp;
        
        Dada ret = new Dada();
        sp = new Globals.Splitter(html);
        html = sp.cropToStr("div id=\"cap\">").getString();
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Wind: ")
                .cropToStrEx(">")
                .getToStrEx("&nbsp;kt");
        ret.wind = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Dir: ")
                .cropToStrEx(">")
                .getToStrEx("<");
        ret.dir = Globals.txtToDir(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Humd: ")
                .cropToStrEx(">")
                .getToStrEx("&nbsp;%");
        ret.humidity = Integer.parseInt(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Temp: ")
                .cropToStrEx(">")
                .getToStrEx("&nbsp;" + (char)65533 + "C");
        ret.temp = Float.parseFloat(sp.getString());
        
        /*sp = new Globals.Splitter(html);
        sp.cropToStr("Pluja Acumulada")
                .cropToStr("<span")
                .cropToStrEx(">")
                .getToStrEx(" l/m");
        ret.rain = Float.parseFloat(sp.getString().replace(",", "."));*/
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Pres: ")
                .cropToStrEx(">")
                .getToStrEx("&nbsp;hPa");
        ret.barometer = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Hora registro: ")
                .cropToStrEx(">")
                .getToStrEx("</span>");
        String[] datetime = sp.getString().split(" ~ ");
        String[] date = datetime[0].trim().split("/");
        String[] time = datetime[1].trim().split(":");
        ret.time = Globals.parseCalendar(date[2], date[1], date[0], time[0], time[1]);
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "cneb";
    }
    
}
