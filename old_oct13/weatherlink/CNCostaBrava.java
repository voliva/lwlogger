/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherlink;

/**
 *
 * @author Victor
 */
public class CNCostaBrava extends WeatherLinkStation {
    
    public CNCostaBrava(){
        registerGust = false; // Perque la ratxa es la ratxa màxima de tot el dia
    }

    @Override
    public String getStationCode() {
        return "cncb";
    }

    @Override
    protected String getSpeedURL() {
        return "http://www.cncostabrava.com/meteo/WindSpeed.gif";
    }

    @Override
    protected String getDirectionURL() {
        return "http://www.cncostabrava.com/meteo/WindDirection.gif";
    }
    
}
