/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

/**
 *
 * @author Victor
 */
public class Cadiz extends DavisWeatherLinkStation {
    // http://www.tiempoencadiz.es/
    

    @Override
    public String getStationCode() {
        return "cadiz";
    }

    @Override
    protected String getDavisUser() {
        return "aguasdecadiz"; // TODO Cambiar a puertoamerica quan funcioni
    }
    
}
