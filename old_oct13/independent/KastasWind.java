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
public class KastasWind implements StationFetcher {

    @Override
    public Dada fetch() {
        Dada ret = new Dada();
        
        String html = Globals.getHTML("http://www.kastaswind.com/met/inicio_files/index.htm");
        if(html == null) return null;
        
        Globals.Splitter sp = new Globals.Splitter(html);
        sp.cropToStrEx("ltima actualizaci");
        html = sp.getString();
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("&nbsp;")
                .getToStrEx(" on");
        String[] time = sp.getString().split(":");
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("on ")
                .getToStrEx("</");
        String[] date = sp.getString().split(" ");
        
        ret.time = Globals.parseCalendar(date[2], Globals.nomMesEspToNum(date[1]).toString(), date[0], time[0], time[1]);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("<td id=\"wlatest\"")
                .cropToStrEx("<b>")
                .getToStrEx(" kts");
        ret.wind = Float.parseFloat(sp.getString().replace(",", "."));
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("<td id=\"wgust\"")
                .cropToStrEx("<b>")
                .getToStrEx(" kts");
        ret.gust = Float.parseFloat(sp.getString().replace(",", "."));
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("<td id=\"wgust\"")
                .cropToStr("<td align=\"center\"")
                .cropToStrEx("<b>")
                .getToStrEx("<");
        ret.dir = Globals.txtToDir(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("Temperatura")
                .cropToStr("<td align=\"center\"")
                .cropToStrEx(">");
        html = sp.getString();
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<b>")
                .getToStrEx(" C");
        ret.temp = Float.parseFloat(sp.getString().replace(",", "."));
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<td align=\"center\"")
                .cropToStrEx(">");
        html = sp.getString();
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<b>")
                .getToStrEx(" hPa");
        ret.barometer = Float.parseFloat(sp.getString().replace(",", "."));
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<td align=\"center\"")
                .cropToStrEx(">");
        html = sp.getString();
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<b>")
                .getToStrEx(" %");
        ret.humidity = Integer.parseInt(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<td align=\"center\"")
                .cropToStrEx(">");
        html = sp.getString();
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<b>")
                .getToStrEx(" mm");
        ret.rain = Float.parseFloat(sp.getString().replace(",", "."));
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "kastaswind";
    }
    
}
