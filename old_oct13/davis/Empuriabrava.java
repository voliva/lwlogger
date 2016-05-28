/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.davis;

import lwlogger.Dada;

/**
 *
 * @author victor
 */
public class Empuriabrava extends DavisWeatherLinkStation {

    @Override
    public String getStationCode() {
        return "empuriabrava";
    }

    @Override
    protected String getDavisUser() {
        return "trastitu";
    }
    
    @Override
    protected void post(Dada dada){
        if(dada.dir != null){
            /* 90->270
             * 180->180
             * 270->90
             */
            //dada.dir = 360 - dada.dir.intValue();
            
            dada.dir = (dada.dir.intValue() + 180) % 360;
        }
    }
}
