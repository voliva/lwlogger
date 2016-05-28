/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.xema;

import java.util.TimeZone;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public abstract class XEMAStationOld implements StationFetcher {
    // TODO totes les estacions per sota un llindar del nivell del mar. 10m? 5m?
    
    @Override
    public Dada fetch() {
        String url = "http://www.meteo.cat/xema/AppJava/Detall24Estacio.do";
        String postfields = "idEstacio=" + getXEMAId();
        
        postfields += "&team=ObservacioTeledeteccio";
        postfields += "&inputSource=DadesActualsEstacio";
        
        String html = Globals.postHTML(url, postfields);
        Dada ret = new Dada();
        
        Globals.Splitter sp = new Globals.Splitter(html);
        sp.cropToStr("Dades de per")
                .cropToStr("<tbody>")
                .cropToStrEx("<tr class=\"odd\">")
                .getToStrEx("</tr>");
        html = sp.getString();
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<span class=odd>")
                .getToStrEx(")");
        String datetime = sp.getString().trim();
        datetime = datetime.substring(0, 11) + datetime.substring(18);
        ret.time = Globals.parseCalendar(datetime, "dd/MM/yyyy HH:mm", TimeZone.getTimeZone("UTC"));
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<span class=firstLebel>");
        html = sp.getString();
        sp.getToStrEx("</span>");
        ret.temp = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        for(int i=0; i<3; i++)
            sp.cropToStrEx("<span class=firstLebel>");
        html = sp.getString();
        sp.getToStrEx("</span>");
        ret.humidity = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<span class=firstLebel>");
        html = sp.getString();
        sp.getToStrEx("</span>");
        ret.rain = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<span class=\"firstLebel\">");
        html = sp.getString();
        sp.getToStrEx(" -");
        ret.wind = Globals.mpsToKnots(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<span class=\"firstLebel\">");
        html = sp.getString();
        sp.getToStrEx("</span>");
        ret.dir = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<span class=firstLebel>");
        html = sp.getString();
        sp.getToStrEx("</span>");
        ret.gust = Globals.mpsToKnots(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<span class=firstLebel>");
        // html = sp.getString();
        sp.getToStrEx("</span>");
        ret.barometer = Globals.mpsToKnots(sp.getString());
        
                
        
        return ret;
    }

    protected abstract String getXEMAId();
}
