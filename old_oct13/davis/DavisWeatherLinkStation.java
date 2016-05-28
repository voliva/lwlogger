/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

import java.util.Calendar;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author victor
 */
public abstract class DavisWeatherLinkStation implements StationFetcher {

    @Override
    public Dada fetch() {
        String html = Globals.getHTML("http://www.weatherlink.com/user/" + getDavisUser() + "/index.php?view=summary&headers=0&type=1");
        if(html == null) return null;
        
        try {
            Dada ret = new Dada();
            ret.time = Calendar.getInstance();
            
            Globals.Splitter sp;
            
            sp = new Globals.Splitter(html);
            sp.cropToStr("Outside Temp")
                    .cropToStr("summary_data")
                    .cropToStrEx(">")
                    .getToStrEx(" C");
            ret.temp = Float.parseFloat(sp.getString());
            
            sp = new Globals.Splitter(html);
            sp.cropToStr("Outside Humidity")
                    .cropToStr("summary_data")
                    .cropToStrEx(">")
                    .getToStrEx("%");
            ret.humidity = Float.parseFloat(sp.getString());
            
            sp = new Globals.Splitter(html);
            sp.cropToStr("Barometer")
                    .cropToStr("summary_data")
                    .cropToStrEx(">")
                    .getToStrEx("mb");
            ret.barometer = Float.parseFloat(sp.getString());
            
            sp = new Globals.Splitter(html);
            sp.cropToStr("Wind Direction")
                    .cropToStr("summary_data")
                    .cropToStr(">")
                    .cropToStrEx(";")
                    .getToStrEx("&deg;");
            ret.dir = Float.parseFloat(sp.getString());
            
            sp = new Globals.Splitter(html);
            sp.cropToStr("Wind Speed")
                    .cropToStr("summary_data")
                    .cropToStrEx(">")
                    .getToStrEx("</td>");
            if(sp.getString().equals("Calm"))
                ret.wind = 0;
            else{
                sp.getToStrEx(" km/h");
                ret.wind = Globals.kmhToKnots(Float.parseFloat(sp.getString()));
            }
            
            sp = new Globals.Splitter(html);
            sp.cropToStr("Wind Gust Speed")
                    .cropToStr("ummary_data")
                    .cropToStr("summary_data")
                    .cropToStrEx(">")
                    .getToStrEx("</td>");
            if(sp.getString().equals("Calm"))
                ret.gust = 0;
            else{
                if(!sp.getString().equals("n/a")){
                    sp.getToStrEx(" km/h");
                    ret.gust = Globals.kmhToKnots(Float.parseFloat(sp.getString()));
                }
            }
            
            sp = new Globals.Splitter(html);
            sp.cropToStr("class=\"summary_data\">Rain")
                    .cropToStr("Rain")
                    .cropToStr("summary_data")
                    .cropToStrEx(">")
                    .getToStrEx("mm/Hour");
            ret.rain = Float.parseFloat(sp.getString());
            
            post(ret); // Si s'ha de fer un tractament especial
            return ret;
        }catch (NumberFormatException ex){
            Globals.logError(getStationCode(), html, ex);
        }
        return null;
    }

    @Override
    public abstract String getStationCode();
    
    protected abstract String getDavisUser();
    
    protected void post(Dada dada){
    }
}
