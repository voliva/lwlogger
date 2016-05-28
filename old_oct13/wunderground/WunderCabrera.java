/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

/**
 *
 * @author Victor
 */
public class WunderCabrera extends WundergroundStation {
    // http://ciezar.homeip.net/weather/wxgauge.php
    
    @Override
    protected String getStationId() {
        return "IBARCELO42";
    }

    @Override
    public String getStationCode() {
        return "wundergr/cabrera";
    }
    
}
