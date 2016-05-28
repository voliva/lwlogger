/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class CNLEscala extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "cnle";
    }

    @Override
    protected String getURL() {
        return "http://www.nauticescala.com/content/conf/clientraw.txt";
    }
    
}
