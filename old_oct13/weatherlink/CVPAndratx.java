/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherlink;

/**
 *
 * @author Victor
 */
public class CVPAndratx extends WeatherLinkStation {

    @Override
    public String getStationCode() {
        return "cvpa";
    }

    @Override
    protected String getSpeedURL() {
        return "http://www.cvpa.es/images/WindSpeed.gif";
    }

    @Override
    protected String getDirectionURL() {
        return "http://www.cvpa.es/images/WindDirection.gif";
    }
    
}
