/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

import java.util.Calendar;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author victor
 */
public abstract class WeatherDisplayStation implements StationFetcher {
    @Override
    public Dada fetch(){
        try {
            String raw = Globals.getHTML(getURL());

            if(raw == null) return null;
            
            // 0 	 1		2	3	4	5		6	7		8		9	...	29	   30	31	...	35	36	...	141
            // 12345 wind gust dir temp humitat bar rain mensual anual 		hora minut segon	dia	mes		any
            String[] arr = raw.split(" ");
            if(arr.length < 142) return null;

            Dada ret = new Dada();
            ret.temp = Globals.parseNumber(arr[4]);
            ret.humidity = Globals.parseNumber(arr[5]);
            ret.barometer = Globals.parseNumber(arr[6]);
            ret.wind = Globals.parseNumber(arr[1]);
            ret.gust = Globals.parseNumber(arr[140]); // Abans era 2
            ret.dir = Globals.parseNumber(arr[3]);
            ret.rain = Globals.parseNumber(arr[7]);
            
            ret.time = parseTime(arr[141], arr[36], arr[35], arr[29], arr[30]);

            return ret;
        }catch (NumberFormatException ex){
            System.err.println(getStationCode() + ": " + ex);
        }
        return null;
    }
    
    protected Calendar parseTime(String year, String month, String day, String hour, String minute){
        return Globals.parseCalendar(year, month, day, hour, minute);
    }
    
    @Override
    public abstract String getStationCode();
    
    protected abstract String getURL();
}
