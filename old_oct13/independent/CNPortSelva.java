/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.independent;

import java.util.Calendar;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public class CNPortSelva implements StationFetcher {

    @Override
    public Dada fetch() {
        String html = Globals.getHTML("http://www.cnps.es/servimet.php");
        if(html == null) return null;
        
        Dada ret = new Dada();
        Globals.Splitter sp;
        
        ret.time = Calendar.getInstance();
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("vent_actual.swf")
                .cropToStrEx("forzavent=")
                .getToStrEx("&");
        ret.wind = Globals.kmhToKnots(Globals.parseNumber(sp).floatValue());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("vent_actual.swf")
                .cropToStrEx("vent_maxima=")
                .getToStrEx("&");
        ret.gust = Globals.kmhToKnots(Globals.parseNumber(sp).floatValue());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("vent_actual.swf")
                .cropToStrEx("graustext=")
                .getToStrEx("&");
        ret.temp = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("vent_actual.swf")
                .cropToStrEx("humitat=")
                .getToStrEx("&");
        ret.humidity = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("vent_actual.swf")
                .cropToStrEx("presio=")
                .getToStrEx("&");
        ret.barometer = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("vent_actual.swf")
                .cropToStrEx("pluja=")
                .getToStrEx("&");
        ret.rain = Globals.parseNumber(sp);
        
        
        if(ret.wind.intValue() > 70){
            return null;
        }
        return ret;
    }

    @Override
    public String getStationCode() {
        return "cnps";
    }
    
}
