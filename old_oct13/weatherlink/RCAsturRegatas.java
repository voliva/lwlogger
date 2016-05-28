/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherlink;

/**
 *
 * @author Victor
 */
public class RCAsturRegatas extends WeatherLinkStation {

    @Override
    public String getStationCode() {
        return "rcar";
    }

    @Override
    protected String getSpeedURL() {
        return "http://www.rcar.es/webcam/WindSpeed.gif";
    }

    @Override
    protected String getDirectionURL() {
        return "http://www.rcar.es/webcam/WindDirection.gif";
    }
    
}
