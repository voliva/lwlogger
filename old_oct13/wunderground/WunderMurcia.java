/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderMurcia extends WundergroundStation {

    @Override
    protected String getStationId() {
        return "LELC";
    }

    @Override
    public String getStationCode() {
        return "wundergr/murcia";
    }
    
}
