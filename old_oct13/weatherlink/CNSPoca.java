/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherlink;

/**
 *
 * @author victor
 */
public class CNSPoca extends WeatherLinkStation {

    @Override
    public String getStationCode() {
        return "cnsp";
    }

    @Override
    protected String getSpeedURL() {
        return "http://www.cnsp.es/app/weatherLink/WindSpeed.gif";
    }

    @Override
    protected String getDirectionURL() {
        return "http://www.cnsp.es/app/weatherLink/WindDirection.gif";
    }
    
}
