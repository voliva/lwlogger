/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class CVBenicassim extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "cvb";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/benicassim/es/Data/wflash2.txt";
    }
    
}
