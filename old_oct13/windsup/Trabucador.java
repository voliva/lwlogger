/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.windsup;

/**
 *
 * @author Victor
 */
public class Trabucador extends WindsupStation {

    @Override
    protected String getURL() {
        return "http://es.winds-up.com/spot-trabucador-windsurf-kitesurf-516-observations-releves-vent.html";
    }

    @Override
    public String getStationCode() {
        return "windsup/trabucador";
    }
    
}
