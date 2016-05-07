/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderPratLlobregat extends WundergroundStation {

    @Override
    protected String getStationId() {
        return "LEBL";
    }

    @Override
    public String getStationCode() {
        return "wundergr/prat";
    }
    
}
