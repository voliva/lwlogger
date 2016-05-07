/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

/**
 *
 * @author victor
 */
public class CMTorredembarra extends DavisWeatherLinkStation {

    @Override
    public String getStationCode() {
        return "cmt";
    }

    @Override
    protected String getDavisUser() {
        return "cmtorredembarra";
    }
    
}
