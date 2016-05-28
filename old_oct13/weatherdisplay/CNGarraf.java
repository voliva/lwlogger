/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class CNGarraf extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "cng";
    }

    @Override
    protected String getURL() {
        return "http://80.67.108.142/clientraw.txt";
    }
    
}
