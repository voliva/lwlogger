/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.datawind;

import lwlogger.Dada;

/**
 *
 * @author victor
 */
public class CMSitges extends DataWindStation {

    @Override
    public Dada fetch() {
        return this.fetchFromImg("http://www.datawind.es/fcv/cms/php/getcms3.php");
    }

    @Override
    public String getStationCode() {
        return "cms";
    }
    
}
