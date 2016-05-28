/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class CNOropesa extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "cno";
    }

    @Override
    protected String getURL() {
        return "http://www.cnoropesa.com/wd/clientraw.txt";
    }
    
}
