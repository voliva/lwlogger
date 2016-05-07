/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderVera extends WundergroundStation {

    @Override
    protected String getStationId() {
        return "IANDALUC56";
    }

    @Override
    public String getStationCode() {
        return "wundergr/vera";
    }
    
}
