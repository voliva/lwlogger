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
public class CNMasnou extends DataWindStation {

    @Override
    public Dada fetch() {
        Dada ret = this.fetchFromImg("http://www.datawind.es/fcv/cnm/php/getcnm3.php");
        if(ret == null || ret.wind.intValue() == 0 && ret.gust.intValue() == 0 && ret.dir.intValue() == 0) return null;
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "cnm";
    }
    
}
