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
 * @author victor
 */
public class CNVilanova implements StationFetcher {

    @Override
    public Dada fetch() {
        String html = Globals.getHTML("http://www.meteocnv.com/directo/datos.htm");
        if(html == null) return null;
        
        Globals.Splitter sp;
        Dada ret = new Dada();
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Ultima actualizaci");
        html = sp.getString();
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("&nbsp;")
                .cropToStrEx(" ")
                .getToStrEx(" ");
        String[] date = sp.getString().split("/");
        if(date.length < 3) return null;
        String year = "20" + date[2];
        String mon = date[1];
        String day = date[0];
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("-")
                .cropToStrEx(" ")
                .getToStrEx(" </font>");
        String[] time = sp.getString().split(":");
        if(time.length < 2) return null;
        String hour = time[0].trim();
        String min = time[1].trim();
        ret.time = Globals.parseCalendar(year, mon, day, hour, min);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Velocidad Viento")
                .cropToStrEx("&nbsp;")
                .getToStrEx(" knots");
        ret.wind = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Max. Vel. Viento")
                .cropToStrEx("&nbsp;")
                .getToStrEx(" knots");
        ret.gust = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Direcci" + (char)65533 + "n Viento")
                .cropToStrEx("&nbsp;")
                .getToStrEx("" + (char)65533);
        ret.dir = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Temperatura")
                .cropToStrEx("&nbsp;")
                .getToStrEx(" ");
        ret.temp = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Bar" + (char)65533 + "metro")
                .cropToStrEx("&nbsp;")
                .getToStrEx(" mb");
        ret.barometer = Float.parseFloat(sp.getString().trim());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Lluvia de Hoy")
                .cropToStrEx("&nbsp;")
                .getToStrEx(" mm");
        ret.rain = Float.parseFloat(sp.getString().trim());
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "cnv";
    }
    
}
