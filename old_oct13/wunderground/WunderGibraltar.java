/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderGibraltar extends WundergroundStation {

    @Override
    protected String getStationId() {
        return "LXGB";
    }

    @Override
    public String getStationCode() {
        return "wundergr/gibraltar";
    }
    
}
