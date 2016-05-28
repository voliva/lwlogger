/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.meteoclimatic;

/**
 *
 * @author Victor
 */
public class MeteoPremia extends MeteoclimaticStation {
    // Meteopremia.com
    
    @Override
    protected String getMeteoclimaticID() {
        return "ESCAT0800000008330B";
    }

    @Override
    public String getStationCode() {
        return "meteopremia";
    }
    
}
