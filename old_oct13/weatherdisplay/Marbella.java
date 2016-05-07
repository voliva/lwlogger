/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class Marbella extends WeatherDisplayStation {
    /* Puerto josé banus */
    // http://www.meteopuertobanus.es/
    
    @Override
    public String getStationCode() {
        return "marbella";
    }

    @Override
    protected String getURL() {
        return "http://www.meteopuertobanus.es/clientraw.txt";
    }
    
}
