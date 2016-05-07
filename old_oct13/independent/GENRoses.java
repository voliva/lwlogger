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
public class GENRoses implements StationFetcher {

    @Override
    public Dada fetch() {
        String html = Globals.getHTML("http://www.genroses.cat/?q=web/meteo");
        if(html == null || html.equals("")) return null;
        
        Dada ret = new Dada();
        Globals.Splitter sp;
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Vent nusos")
                .cropToStrEx("<td class=\"d\">")
                .getToStrEx("</td>");
        ret.wind = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Vent max. nusos")
                .cropToStrEx("<td class=\"d\">")
                .getToStrEx("</td>");
        ret.gust = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Direccio")
                .cropToStrEx("<td class=\"d\">")
                .getToStrEx("</td>");
        ret.dir = Globals.txtToDir(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Temperatura")
                .cropToStrEx("<td class=\"d\">")
                .getToStrEx("</td>");
        ret.temp = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Pressio hPa")
                .cropToStrEx("<td class=\"d\">")
                .getToStrEx("</td>");
        ret.barometer = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("actualitzat: ")
                .getToStrEx("<table");
        String[] dateTime = sp.getString().split(" ");
        if(dateTime.length < 2) return null;
        String[] date = dateTime[0].split("\\.");
        String[] time = dateTime[1].split(":");
        ret.time = Globals.parseCalendar(date[2], date[1], date[0], time[0], time[1]);
        
        if(ret.temp.equals(Float.parseFloat("-17.7")) && ret.wind.doubleValue() == 0 && ret.gust.doubleValue() == 0) return null;
        if(ret.temp.intValue() > 50) return null;
        if(ret.wind.floatValue() < 0) return null;
        if(ret.gust.floatValue() < 0) return null;
        
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "genr";
    }
    
}
