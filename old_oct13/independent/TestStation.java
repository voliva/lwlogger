/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.independent;

import java.util.Calendar;
import lwlogger.Dada;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public class TestStation implements StationFetcher {

    @Override
    public Dada fetch() {
        Dada d = new Dada();
        d.time = Calendar.getInstance();
        d.time.set(Calendar.HOUR_OF_DAY, 19);
        d.time.set(Calendar.MINUTE, 30);
        d.barometer = 89;
        d.dir = 2;
        d.gust = 3;
        d.humidity = 4;
        d.rain = 5;
        d.temp = 6;
        d.wind = 7;
        return d;
    }

    @Override
    public String getStationCode() {
        return "test";
    }
    
}
