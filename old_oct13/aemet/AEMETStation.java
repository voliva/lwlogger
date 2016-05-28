/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.aemet;

import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;

/**
 *
 * @author Victor
 */
public abstract class AEMETStation implements StationFetcher {

    public Dada fetchOld() {
        String html = Globals.getHTML("http://www.aemet.es/es/eltiempo/observacion/ultimosdatos?k=cat&l=" + getIndClimatologico() + "&w=0&datos=det&x=h24&f=vel_viento");
        if(html == null) return null;
        
        Dada ret = new Dada();
        
        Globals.Splitter sp = new Globals.Splitter(html);
        sp.cropToStrEx("<table id=\"table\"")
            .cropToStrEx("<tbody>")
            .cropToStrEx(">") // Aixo es carrega el <tr>
            .getToStrEx("</tbody>");
        html = sp.getString();
        
        // Pre: html comença amb <td style..>hora/data</td>
        do {
            sp.cropToStrEx(">")
                    .getToStrEx("</td>");
            String _datetime = sp.getString(); // --------------------
            String[] datetime = _datetime.split(" ");
            String[] date = datetime[0].split("/");
            String[] time = datetime[1].split(":");
            ret.time = Globals.parseCalendar(date[2], date[1], date[0], time[0], time[1]);

            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<td >");
            html = sp.getString();
            sp.getToStrEx("</td>");
            String temp = sp.getString(); // ---------------------
            ret.temp = Globals.parseNumber(temp);

            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<td >");
            html = sp.getString();
            sp.getToStrEx("</td>");
            String wind = sp.getString(); // ---------------------
            if(wind.equals("&nbsp;") || wind.equals("")) return null;
            ret.wind = Globals.kmhToKnots(wind);

            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<td >");
            html = sp.getString();
            sp.getToStrEx("</td>")
                    .cropToStrEx("iconos_viento_udat/")
                    .getToStrEx(".png");
            String dir = sp.getString(); // ---------------------
            if(dir.equals("C")){
                ret.dir = null;
            }else{
                ret.dir = Globals.txtToDir(dir);
            }

            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<td >");
            html = sp.getString();
            sp.getToStrEx("</td>");
            String gust = sp.getString(); // ---------------------
            ret.gust = Globals.kmhToKnots(gust);

            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<td >");
            sp.cropToStrEx("<td >");
            html = sp.getString();
            sp.getToStrEx("</td>");
            String rain = sp.getString(); // ---------------------
            ret.rain = Globals.parseNumber(rain);

            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<td >");
            html = sp.getString();
            sp.getToStrEx("</td>");
            String barometer = sp.getString(); // ---------------------
            ret.barometer = Globals.parseNumber(barometer);

            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<td >");
            sp.cropToStrEx("<td >");
            html = sp.getString();
            sp.getToStrEx("</td>");
            String humidity = sp.getString(); // ---------------------
            ret.humidity = Globals.parseNumber(humidity);
            
            sp = new Globals.Splitter(html);
            sp.cropToStrEx("<tr>");
            html = sp.getString();
        } while (ret.wind == null);
        
        return ret;
    }

    @Override
    public Dada fetch() {
        String html = Globals.getHTML("http://www.aemet.es/es/eltiempo/observacion/ultimosdatos_0281Y_datos-horarios.csv?k=cat&l=" + getIndClimatologico() + "&datos=det&w=0&f=vel_viento&x=h24");
        if(html == null) return null;
        
        Dada ret = new Dada();
        
        String[] lines = html.split("\r\n");
        for(int i=4; i<lines.length && (ret.wind == null || ret.time == null); i++){
            String[] values = lines[i].split(",");
            if(values.length < 9) continue;
            
            for(int j=0; j<values.length; j++){
                values[j] = values[j].replace("\"", "");
            }
            
            // 05/07/2013 20:00
            ret.time = Globals.parseCalendar(values[0], "dd/MM/yyyy HH:mm");
            ret.temp = Globals.parseNumber(values[1]);
            ret.wind = Globals.kmhToKnots(values[2]);
            if(values[3].equals("Calma")){
                ret.dir = null;
            }else{
                ret.dir = Globals.txtToDir(values[3]);
            }
            ret.gust = Globals.kmhToKnots(values[4]);
            ret.rain = Globals.parseNumber(values[6]);
            ret.barometer = Globals.parseNumber(values[7]);
            ret.humidity = Globals.parseNumber(values[9]);
        }
        
        return ret;
    }

    @Override
    public String getStationCode(){
        return "aemet/" + getPoblacio();
    }
    
    public abstract String getIndClimatologico();
    
    public abstract String getPoblacio();
    
}
