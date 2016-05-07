/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

/**
 *
 * @author victor
 */
public class MeteoCullera extends DavisWeatherLinkStation {

    @Override
    public String getStationCode() {
        return "meteocullera";
    }

    @Override
    protected String getDavisUser() {
        return "MeteoCullera";
    }
    
}
