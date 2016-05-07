/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class StPerePescador extends WeatherDisplayStation {

    @Override
    public String getStationCode() {
        return "stperepescador";
    }

    @Override
    protected String getURL() {
        return "http://www.ballena-alegre.com/meteo/clientraw.txt";
    }
    
}
