/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderEscala extends WundergroundStation {
    //http://www.meteolescala.com/Meteolescala/www.meteolescala.com.html
    
    @Override
    protected String getStationId() {
        return "IGIRONAL6";
    }

    @Override
    public String getStationCode() {
        return "wundergr/escala";
    }
    
}
