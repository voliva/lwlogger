/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class CNPortAro extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "cnpa";
    }

    @Override
    protected String getURL() {
        return "http://www.clubnauticportdaro.cat/meteo/clientraw.txt";
    }
    
}
