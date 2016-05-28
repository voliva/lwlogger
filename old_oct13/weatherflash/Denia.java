/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherflash;

/**
 *
 * @author Victor
 */
public class Denia extends WeatherFlashStation {

    @Override
    public String getStationCode() {
        return "denia";
    }

    @Override
    protected String getURL() {
        return "http://www.comunitatvalenciana.com/meteo/denia-puerto2/es/Data/wflash2.txt";
    }
    
}
