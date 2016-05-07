/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.xema;

/**
 *
 * @author Victor
 */
public class Tarragona extends XEMAStation {

    @Override
    protected String getXEMAId() {
        return "XE";
    }

    @Override
    public String getStationCode() {
        return "xema/tarragona";
    }

    @Override
    protected int getComarca() {
        return TARRAGONES;
    }
    
}
