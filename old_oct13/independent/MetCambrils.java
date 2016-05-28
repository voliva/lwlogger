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
public class MetCambrils implements StationFetcher {

    @Override
    public Dada fetch() {
        try {
            Dada ret = new Dada();

            String html = Globals.getHTML("http://www.alonsoalonso.cat/MeteoCambrils/");
            if(html == null) return null;

            Globals.Splitter sp = new Globals.Splitter(html);
            sp.cropToStr("Temps actual a")
                    .cropToStr("Brown");
            html = sp.getString();

            sp = new Globals.Splitter(html);
            sp.cropToStr("Temperatura")
                    .cropToStrEx("<font color=\"#3366FF\">")
                    .getToStrEx((char)65533 + "C");
            try {
                ret.temp = Float.parseFloat(sp.getString());
            } catch (NumberFormatException ex){ }


            sp = new Globals.Splitter(html);
            sp.cropToStr("Humitat")
                    .cropToStrEx("<font color=\"#3366FF\">")
                    .getToStrEx("%");
            try {
                ret.humidity = Integer.parseInt(sp.getString());
            } catch (NumberFormatException ex){ }


            sp = new Globals.Splitter(html);
            sp.cropToStr("Vent")
                    .cropToStr("<font color=\"#3366FF\">")
                    .cropToStrEx("<small>")
                    .getToStrEx(" km/h");
            String[] dirWind = sp.getString().split(" ");
            try {
                ret.wind = Globals.kmhToKnots(Float.parseFloat(dirWind[2]));
            } catch (NumberFormatException ex){ }
            try {
                ret.dir = Globals.txtToDir(dirWind[0]);
            } catch (NumberFormatException ex){ }

            sp = new Globals.Splitter(html);
            sp.cropToStr("Pressi")
                    .cropToStrEx("<font color=\"#3366FF\">")
                    .getToStrEx("&nbsp;hPa");
            try {
                ret.barometer = Float.parseFloat(sp.getString());
            } catch (NumberFormatException ex){ }

            sp = new Globals.Splitter(html);
            sp.cropToStr("Intensitat pluja actual")
                    .cropToStrEx("<font color=\"#3366FF\">")
                    .getToStrEx("&nbsp;mm");
            try {
                ret.rain = Float.parseFloat(sp.getString());
            } catch (NumberFormatException ex){ }

            ret.time = Calendar.getInstance();

            return ret;
        }catch (ArrayIndexOutOfBoundsException ex){
            return null;
        }
    }

    @Override
    public String getStationCode() {
        return "metcambrils";
    }
    
}
