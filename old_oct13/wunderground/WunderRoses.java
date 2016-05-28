/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderRoses extends WundergroundStation {
    // http://www.meteoportestrella.es/ => No es pot demanar permis. Pues a saco!
    
    @Override
    protected String getStationId() {
        return "ICATALUA44";
    }

    @Override
    public String getStationCode() {
        return "wundergr/roses";
    }
    
}
