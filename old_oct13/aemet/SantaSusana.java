/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.aemet;

/**
 *
 * @author Victor
 */
public class SantaSusana extends AEMETStation {
    @Override
    public String getIndClimatologico() {
        return "0255B";
    }

    @Override
    public String getPoblacio() {
        return "santasusana";
    }
}
