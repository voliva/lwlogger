/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class Piles extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "piles";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/piles/es/Data/wflash2.txt";
    }
    
}
