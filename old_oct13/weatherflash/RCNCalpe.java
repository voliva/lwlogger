/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class RCNCalpe extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "rcnc";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/calpe/es/Data/wflash2.txt";
    }
    
}
