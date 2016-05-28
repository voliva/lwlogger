/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.meteoclimatic;

/**
 *
 * @author Victor
 */
public class CVCanet extends MeteoclimaticStation {

    @Override
    protected String getMeteoclimaticID() {
        return "ESCAT0800000008360A";
    }

    @Override
    public String getStationCode() {
        return "cvcm";
    }
    
}
