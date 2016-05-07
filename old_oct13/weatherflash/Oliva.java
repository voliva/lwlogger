/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class Oliva extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "oliva";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/olivap/es/Data/wflash2.txt";
    }
    
}
