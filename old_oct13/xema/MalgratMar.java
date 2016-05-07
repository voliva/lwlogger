/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.xema;

/**
 *
 * @author Victor
 */
public class MalgratMar extends XEMAStation {

    @Override
    protected String getXEMAId() {
        return "WT";
    }

    @Override
    public String getStationCode() {
        return "xema/malgrat";
    }

    @Override
    protected int getComarca() {
        return MARESME;
    }
    
}
