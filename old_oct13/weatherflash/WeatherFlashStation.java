/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

import java.util.Calendar;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public abstract class WeatherFlashStation implements StationFetcher {

    @Override
    public Dada fetch() {
        String raw = Globals.getHTML(getURL());
        if(raw == null) return null;
        
        String[] arr = raw.split(",");
        Dada ret = new Dada();
        
        if(arr[0].length() < 2) return null;
        String[] time = arr[0].substring(2).split(":");
        time[0] = time[0].replace("+", "0");
        String[] date = arr[275].split("/");
        ret.time = Globals.parseCalendar(date[2], date[1], date[0], time[0], time[1]);
        
        long currentTime = Calendar.getInstance().getTime().getTime();
        if(ret.time.getTime().getTime() > currentTime + 30 * 60000){
            return null;
        }   
        
        ret.dir = Float.parseFloat(arr[2]);
        ret.wind = Globals.mphToKnots(Float.parseFloat(arr[3]));
        ret.humidity = Float.parseFloat(arr[6]);
        ret.temp = Globals.FtoC(Float.parseFloat(arr[8]));
        ret.barometer = Globals.inhgTombar(Float.parseFloat(arr[9]));
        ret.rain = Globals.inTomm(Float.parseFloat(arr[262]));
        
        return ret;
    }

    @Override
    public abstract String getStationCode();
    
    protected abstract String getURL();
}
