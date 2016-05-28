/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.windsup;

/**
 *
 * @author Victor
 */
public class Riumar extends WindsupStation {

    @Override
    protected String getURL() {
        return "http://es.winds-up.com/spot-riumar-la-marquesa-windsurf-kitesurf-455-observations-releves-vent.html";
    }

    @Override
    public String getStationCode() {
        return "windsup/riumar";
    }
    
}
