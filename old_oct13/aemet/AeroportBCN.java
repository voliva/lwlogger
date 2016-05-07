/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.aemet;

/**
 *
 * @author Victor
 */
public class AeroportBCN extends AEMETStation {

    @Override
    public String getIndClimatologico() {
        return "0076";
    }

    @Override
    public String getPoblacio() {
        return "aeroportprat";
    }
    
}
