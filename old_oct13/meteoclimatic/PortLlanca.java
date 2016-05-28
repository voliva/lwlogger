/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.meteoclimatic;

/**
 *
 * @author Victor
 */
public class PortLlanca extends MeteoclimaticStation {

    @Override
    protected String getMeteoclimaticID() {
        return "ESCAT1700000017490A";
    }

    @Override
    public String getStationCode() {
       return "portllanca";
    }
    
}
