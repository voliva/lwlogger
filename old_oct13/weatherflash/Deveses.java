/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class Deveses extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "deveses";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/denia-deveses/es/Data/wflash2.txt";
    }
    
}
