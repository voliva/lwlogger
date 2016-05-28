/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherlink;

/**
 *
 * @author Victor
 */
public class CVCanetMar extends WeatherLinkStation {
    
    /*@Override
    public Dada fetch(){
        return null;
    }*/

    @Override
    public String getStationCode() {
        return "cvcm";
    }

    @Override
    protected String getSpeedURL() {
        return "http://www.clubvelacanet.com/WeatherUpload/WindSpeed.gif";
    }

    @Override
    protected String getDirectionURL() {
        return "http://www.clubvelacanet.com/WeatherUpload/WindDirection.gif";
    }
    
}
