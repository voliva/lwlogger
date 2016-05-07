/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderBadalona extends WundergroundStation {

    @Override
    protected String getStationId() {
        return "ICTBADAL3";
    }

    @Override
    public String getStationCode() {
        return "wundergr/badalona";
    }
    
}
