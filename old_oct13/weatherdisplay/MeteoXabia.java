/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

import java.util.Calendar;
import java.util.TimeZone;
import lwlogger.Globals;

/**
 *
 * @author Victor
 */
public class MeteoXabia extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "xabia";
    }

    @Override
    protected String getURL() {
        return "http://www.meteoxabia.com/clientraw.txt";
    }
    
    @Override
    protected Calendar parseTime(String year, String month, String day, String hour, String minute){
        return Globals.parseCalendar(TimeZone.getTimeZone("UTC"), year, month, day, hour, minute);
    }
    
}
