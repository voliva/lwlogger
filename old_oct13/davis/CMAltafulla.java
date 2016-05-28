/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

import lwlogger.Dada;
import lwlogger.datawind.DataWindStation;

/**
 *
 * @author victor
 */
public class CMAltafulla extends DavisWeatherLinkStation {

    /*@Override
    public Dada fetch() {
        return this.fetchFromBook("http://www.datawind.es/fcv/cma/book/php/lastdata.php");
    }*/
    

    @Override
    public String getStationCode() {
        return "cma";
    }
    
    @Override
    protected String getDavisUser() {
        return "cmaltafulla";
    }
    
}
