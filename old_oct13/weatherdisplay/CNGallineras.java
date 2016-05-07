/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class CNGallineras extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "cnga";
    }

    @Override
    protected String getURL() {
        return "http://www.cngallineras.es/meteo/clientraw.txt";
    }
    
}
