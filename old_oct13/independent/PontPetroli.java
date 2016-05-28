/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.independent;

import java.util.TimeZone;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public class PontPetroli implements StationFetcher {

    @Override
    public Dada fetch() {
        Dada ret = new Dada();
        
        String html = Globals.getHTML("http://www.pontdelpetroli.org/webcam.aspx");
        if(html == null) return null;
        
        Globals.Splitter sp = new Globals.Splitter(html);
        sp.cropToStr("<div id=\"lateral1-2\" class=\"lateral1-2\">");
        html = sp.getString();
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Velocitat mitjana del Vent")
                .cropToStr("<span")
                .cropToStrEx(">")
                .getToStrEx(" m/s");
        try {
            ret.wind = Globals.mpsToKnots(Float.parseFloat(sp.getString().replace(",", ".")));
        } catch (NumberFormatException ex){}
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Direcci")
                .cropToStr("<span")
                .cropToStrEx(">")
                .getToStrEx("</span>");
        try {
            ret.dir = Integer.parseInt(sp.getString().substring(0, sp.getString().length()-1));
        } catch (NumberFormatException ex){}
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Humitat Relativa")
                .cropToStr("<span")
                .cropToStrEx(">")
                .getToStrEx(" %");
        try {
            ret.humidity = Integer.parseInt(sp.getString());
        } catch (NumberFormatException ex){}
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Temperatura Aparent")
                .cropToStr("<span")
                .cropToStrEx(">")
                .getToStrEx(" ºC");
        try {
            ret.temp = Float.parseFloat(sp.getString().replace(",", "."));
        } catch (NumberFormatException ex){}
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Pluja Acumulada")
                .cropToStr("<span")
                .cropToStrEx(">")
                .getToStrEx(" l/m");
        try{
            ret.rain = Float.parseFloat(sp.getString().replace(",", "."));
        }catch (NumberFormatException ex){
        }
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("id=\"ctl00_Label1\"")
                .cropToStr("<font face")
                .cropToStrEx(">")
                .getToStrEx(" </font>"); // WTF?
                //.getToStrEx(" </span>");
        String date[] = sp.getString().split("/");
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("id=\"ctl00_Label19\"")
                .cropToStrEx("(")
                .getToStrEx(" GMT");
        String time[] = sp.getString().split(":");
        
        ret.time = Globals.parseCalendar(TimeZone.getTimeZone("GMT"), date[2], date[1], date[0], time[0], time[1]);
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "pontpetroli";
    }
    
}
