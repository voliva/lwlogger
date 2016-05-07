/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class Altea extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "altea";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/altea/es/Data/wflash2.txt";
    }
    
}
