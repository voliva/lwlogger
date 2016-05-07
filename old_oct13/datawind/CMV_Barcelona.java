/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.datawind;

import lwlogger.Dada;

/**
 *
 * @author Victor
 */
public class CMV_Barcelona extends DataWindStation {

    @Override
    public Dada fetch() {
        return this.fetchFromXML("http://datawind.es/fcv/bcn/xml/last_minute.php?key=4b9c1b550a0b775e935137f49cff03ef");
    }

    @Override
    public String getStationCode() {
        return "cmv";
    }
    
}
