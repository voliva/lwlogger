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
public class WindOliva implements StationFetcher {

    @Override
    public Dada fetch() {
        Dada ret = new Dada();
        
        String html = Globals.getHTML("http://www.windoliva.com/");
        if(html == null) return null;
        
        Globals.Splitter sp = new Globals.Splitter(html);
        sp.cropToStrEx("Actualizado ");
        html = sp.getString();
        
        sp = new Globals.Splitter(html);
        sp.getToStrEx(" a ");
        String[] date = sp.getString().split("/");
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<b>")
                .getToStrEx("h.</b>");
        String[] time = sp.getString().split(":");
        
        try {
            ret.time = Globals.parseCalendar(date[2], date[1], date[0], time[0], time[1]);
        }catch (ArrayIndexOutOfBoundsException ex){
            return null;
        }
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("<font size=10>")
                .cropToStrEx("-->")
                .getToStrEx("<font");
        ret.wind = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<font size=2>^")
                .getToStrEx("</font>");
        ret.gust = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("<font size=10>")
                .cropToStrEx("<font size=10>")
                .getToStrEx("</font>");
        ret.dir = Globals.txtToDir(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("Temperatura: ")
                .getToStrEx("C");
        ret.temp = Float.parseFloat(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStrEx("Presi")
                .cropToStrEx("n: ")
                .getToStrEx(" mB");
        ret.barometer = Float.parseFloat(sp.getString());
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "windoliva";
    }
    
}
