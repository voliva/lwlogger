/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.xema;

/**
 *
 * @author Victor
 */
public class PratLlobregat extends XEMABaixLlobregatStation {

    @Override
    protected String getXEMAId() {
        return "XL";
    }

    @Override
    public String getStationCode() {
        return "xema/prat";
    }
    
}
