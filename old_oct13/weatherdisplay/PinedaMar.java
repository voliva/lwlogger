/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.weatherdisplay;

/**
 *
 * @author Victor
 */
public class PinedaMar extends WeatherDisplayStation {
    // Pinedasensefils.cat -> Nasinandes http://can.nandes.cat/?

    @Override
    public String getStationCode() {
        return "pinedamar";
    }

    @Override
    protected String getURL() {
        return "http://serveis.pinedasensefils.cat/clientraw.txt";
    }
    
}
