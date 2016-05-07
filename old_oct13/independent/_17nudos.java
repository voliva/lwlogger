/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.independent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public class _17nudos implements StationFetcher {

    @Override
    public Dada fetch() {
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Referer", "http://www.17nudos.com/index.php");
        String html = Globals.getHTML("http://www.17nudos.com/update_me.php", headers);
        
        if(html == null) return null;
        
        Dada ret = new Dada();
        Globals.Splitter sp;
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("viento.gif")
                .cropToStrEx("<br>")
                .getToStrEx("</td>");
        if(!sp.getString().contains("Unknown"))
            ret.wind = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("racha.gif")
                .cropToStrEx("<br>")
                .getToStrEx("</td>");
        if(!sp.getString().contains("Unknown"))
            ret.gust = Globals.parseNumber(sp);
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("direccion.gif")
                .cropToStrEx("<br>")
                .getToStrEx("</td>");
        if(!sp.getString().contains("Unknown"))
            ret.dir = Globals.txtToDir(sp.getString());
        
        sp = new Globals.Splitter(html);
        sp.cropToStr("Actualizado")
                .cropToStrEx("<br> ")
                .getToStrEx(" <br>");
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMM yyyy", new Locale("en", "EN"));
        ret.time = new GregorianCalendar();
        try {
            ret.time.setTime(sdf.parse(sp.getString()));
        } catch (ParseException ex) {
            return null;
        }
        
        return ret;
    }

    @Override
    public String getStationCode() {
        return "17nudos";
    }
    
}
