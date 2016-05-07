/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class RCNGandia extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "rcng";
    }

    @Override
    protected String getURL() {
        return "http://www.meteogandia.com/clientraw.txt";
    }
    
}
