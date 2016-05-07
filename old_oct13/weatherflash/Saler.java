/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class Saler extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "cvd";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/saler/es/Data/wflash2.txt";
    }
    
}
