/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

/**
 *
 * @author victor
 */
public class MrCadaques extends DavisWeatherLinkStation {

    @Override
    public String getStationCode() {
        return "mrcadaques";
    }

    @Override
    protected String getDavisUser() {
        return "mrcadaques";
    }
    
}
