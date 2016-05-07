/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

/**
 *
 * @author victor
 */
public class PortSitges extends DavisWeatherLinkStation {

    @Override
    public String getStationCode() {
        return "portsitges";
    }

    @Override
    protected String getDavisUser() {
        return "portdesitges";
    }
    
}
