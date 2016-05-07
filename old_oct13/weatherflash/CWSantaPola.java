/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class CWSantaPola extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "cwsp";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/santa-pola/es/Data/wflash2.txt";
    }
    
}
