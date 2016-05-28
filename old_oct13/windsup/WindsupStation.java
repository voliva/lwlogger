/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.windsup;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public abstract class WindsupStation implements StationFetcher {

    
    @Override
    public Dada fetch() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "Mozilla/5.0");
        
        String html = Globals.getHTML(getURL(), headers);
        if(html == null) return null;
        
        Globals.Splitter sp = new Globals.Splitter(html);
        sp.cropToStr("type:\"areasplinerange\"")
                .cropToStrEx("data: [")
                .getToStrEx("]");
        String[] data = sp.getString().split("\\},\\{");
        
        int i=data.length-1;
        for(; i >= 0 && data[i].contains("abonnement"); i--){
        }
        
        if(i < 0) return null;
        
        String[] line = data[i].split(", ");

        String time = line[0].substring(2);

        Dada ret = new Dada();

        long _time = Long.parseLong(time);
        TimeZone tz = TimeZone.getTimeZone("Europe/Madrid");
        _time = _time - tz.getOffset(_time);
        
        ret.time = new GregorianCalendar();
        ret.time.setTimeInMillis(_time);
        ret.gust = Globals.parseNumber(line[2].substring(5));
        ret.wind = Globals.parseNumber(line[3].substring(4));

        sp = new Globals.Splitter(html);
        sp.cropToStr("type:\"areasplinerange\"")
                .cropToStr("type:\"column\"")
                .cropToStrEx("data: [")
                .getToStrEx("]")
                .cropToStr("x:" + time)
                .cropToStrEx("o:\"")
                .getToStrEx("\"");
        ret.dir = Globals.txtToDir(sp.getString());

        return ret;
    }

    protected abstract String getURL();
}
