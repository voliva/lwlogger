/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.xema;

/**
 *
 * @author Victor
 */
public class StPerePescadorX extends XEMAStation {

    @Override
    protected String getXEMAId() {
        return "U2";
    }

    @Override
    public String getStationCode() {
        return "xema/stperepescador";
    }

    @Override
    protected int getComarca() {
        return ALT_EMPORDA;
    }
    
}
