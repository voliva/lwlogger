/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author victor
 */
public class CNEstartit extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "cne";
    }

    @Override
    protected String getURL() {
        return "http://www.cnestartit.es/webphp/clientraw.txt";
    }
    
}
