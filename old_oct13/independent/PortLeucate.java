/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.independent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public class PortLeucate implements StationFetcher {

    @Override
    public Dada fetch() {
        Dada ret = new Dada();
        
        String txt;
        int t = 0;
        do {
            if(t > 0){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                }
            }
            t++;
            
            txt = Globals.getHTML("http://www.leucate-port.fr/meteo/data.txt?time=0");
        } while((txt == null || txt.length() == 0 || txt.charAt(0) == '[') && t <= 60/5);
        if(txt == null || txt.length() == 0 || txt.charAt(0) == '[') return null;
        
        String[] aux = txt.split("&");
        Map<String, String> params = new HashMap<String, String>();
        for(int i=0; i<aux.length; i++){
            if(!aux[i].contains("=")) continue;
            String[] aux2 = aux[i].split("=");
            params.put(aux2[0], aux2[1]);
        }
        
        String time = params.get("t");
        ret.time = Globals.parseCalendar(time, "dd/MM/yyyy HH:mm");
        
        String wind = params.get("v9");
        ret.wind = Globals.parseNumber(wind);
        
        String gust = params.get("v11");
        ret.gust = Globals.parseNumber(gust);
        
        String dir = params.get("v10");
        ret.dir = Globals.parseNumber(dir);
        
        String pressure = params.get("v0");
        ret.barometer = Globals.parseNumber(pressure);
        
        String temp = params.get("v5");
        ret.temp = Globals.parseNumber(temp);
        
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "leucate";
    }
    
}
