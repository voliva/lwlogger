/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class CNBasetes extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "cnb";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/benissa/es/Data/wflash2.txt";
    }
    
}
