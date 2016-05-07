/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderTorrevieja extends WundergroundStation {
    // http://www.eltiempoentorrevieja.es/
    
    @Override
    protected String getStationId() {
        return "IALICANT59";
    }

    @Override
    public String getStationCode() {
        return "wundergr/torrevieja";
    }
    
}
