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
public abstract class XEMAStation implements StationFetcher {
    // TODO totes les estacions per sota un llindar del nivell del mar. 10m? 5m?
    protected static int TARRAGONES = 36;
    protected static int ALT_EMPORDA = 2;
    protected static int MARESME = 21;
    protected static int BAIX_LLOBREGAT = 11;
    protected static int MONTSIA = 22;    
    
    @Override
    public Dada fetch() {
        String url = "http://www.meteo.cat/xema/AppJava/Mapper.do";
        String postfields = "id=" + getComarca();
        
        postfields += "&team=ObservacioTeledeteccio";
        postfields += "&inputSource=SeleccioPerComarca";
        
        String html = Globals.postHTML(url, postfields);
        if(html == null) return null;
        
        Globals.Splitter sp = new Globals.Splitter(html);
        sp.cropToStr("ltimes Dades")
                .cropToStr("<tbody>")
                .getToStrEx("</tbody>");
        html = sp.getString();
        
        do {
            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<tr");
            html = sp.getString();
            sp.getToStrEx("</tr>");
            
            if(sp.getString().contains("envia('" + getXEMAId() + "'")){
                Dada ret = new Dada();
                
                for(int i=0; i<3; i++)
                    sp.cropToStrEx("<td");
                sp.cropToStrEx(">");
                html = sp.getString();
                sp.getToStrEx("</td>");
                
                String datetime = sp.getString().trim();
                datetime = datetime.substring(0, 10) + " " + datetime.substring(22);
                ret.time = Globals.parseCalendar(datetime, "dd/MM/yyyy HH:mm)", TimeZone.getTimeZone("UTC"));
                
                sp = new Globals.Splitter(html);
                sp.cropToStrEx("<span")
                        .cropToStrEx(">");
                html = sp.getString();
                sp.getToStrEx("</span>");
                if(!sp.getString().contains("s/d"))
                    ret.temp = Globals.parseNumber(sp);
                
                sp = new Globals.Splitter(html);
                for(int i=0; i<3; i++)
                    sp.cropToStrEx("<span");
                sp.cropToStrEx(">");
                html = sp.getString();
                sp.getToStrEx("</span>");
                if(!sp.getString().contains("s/d"))
                    ret.humidity = Globals.parseNumber(sp);
                
                sp = new Globals.Splitter(html);
                sp.cropToStrEx("<span")
                        .cropToStrEx(">");
                html = sp.getString();
                sp.getToStrEx("</span>");
                if(!sp.getString().contains("s/d"))
                    ret.rain = Globals.parseNumber(sp);
                
                sp = new Globals.Splitter(html);
                sp.cropToStrEx("<span")
                        .cropToStrEx(">");
                html = sp.getString();
                sp.getToStrEx(" -");
                if(!sp.getString().contains("s/d"))
                    ret.wind = Globals.mpsToKnots(sp.getString().trim());
                
                
                sp = new Globals.Splitter(html);
                sp.cropToStrEx("<span")
                        .cropToStrEx(">");
                html = sp.getString();
                sp.getToStrEx("</span>");
                if(!sp.getString().contains("s/d"))
                    ret.dir = Globals.parseNumber(sp);
                
                sp = new Globals.Splitter(html);
                for(int i=0; i<2; i++)
                    sp.cropToStrEx("<span");
                sp.cropToStrEx(">");
                html = sp.getString();
                sp.getToStrEx("<i>");
                if(!sp.getString().contains("s/d"))
                    ret.gust = Globals.mpsToKnots(sp.getString().trim());
                
                sp = new Globals.Splitter(html);
                sp.cropToStrEx("<span")
                        .cropToStrEx(">");
                // html = sp.getString();
                sp.getToStrEx("</span>");
                if(!sp.getString().contains("s/d"))
                    ret.barometer = Globals.mpsToKnots(sp.getString().trim());
                return ret;
            }
        } while (html.contains("<tr"));
                
        return null;
    }

    protected abstract String getXEMAId();
    protected abstract int getComarca();
}
