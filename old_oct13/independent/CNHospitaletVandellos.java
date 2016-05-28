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
public class CNHospitaletVandellos implements StationFetcher {

    @Override
    public Dada fetch() {
        Dada ret = new Dada();
        
        String raw = Globals.getHTML("http://www.vandellos-hospitalet.org/temps/hospi/downld02.txt");
        if(raw == null) return null;
        
        String[] lines = raw.split("\n");
        String[] arr = lines[lines.length-1].trim().replaceAll(" +", " ").split(" ");
        
        //kmhToKnots
        try {
            ret.wind = Globals.kmhToKnots(Float.parseFloat(arr[7]));
        }catch (NumberFormatException ex){}
        try {
            ret.gust = Globals.kmhToKnots(Float.parseFloat(arr[10]));
        }catch (NumberFormatException ex){}
        ret.dir = Globals.txtToDir(arr[8]);
        try {
            ret.humidity = Integer.parseInt(arr[5]);
        }catch (NumberFormatException ex){}
        try {
            ret.barometer = Float.parseFloat(arr[15]);
        }catch (NumberFormatException ex){}
        try {
            ret.rain = Float.parseFloat(arr[16]);
        }catch (NumberFormatException ex){}
        
        String[] date = arr[0].split("/");
        String[] time = arr[1].split(":");
        ret.time = Globals.parseCalendar("20" + date[2], date[1], date[0], time[0], time[1]);
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "cnhv";
    }
    
}
