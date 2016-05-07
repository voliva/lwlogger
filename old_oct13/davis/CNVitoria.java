/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

/**
 *
 * @author Victor
 */
public class CNVitoria extends DavisWeatherLinkStation {

    @Override
    public String getStationCode() {
        return "cnvi";
    }

    @Override
    protected String getDavisUser() {
        return "cnvitoria";
    }
    
}
