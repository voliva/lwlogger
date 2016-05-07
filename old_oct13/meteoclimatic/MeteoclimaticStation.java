/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.meteoclimatic;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Victor
 */
public abstract class MeteoclimaticStation implements StationFetcher {
    private Dada ret = null;
    
    private void publishData(Dada data){
        ret = data;
    }
    
    @Override
    public Dada fetch() {
        
        DefaultHandler handler = new DefaultHandler(){
            ArrayList<String> stack;
            String unit = null;
            Dada aux = null;
            
            @Override
            public void startDocument(){
                stack = new ArrayList<String>();
                aux = new Dada();
            }
            
            @Override
            public void endDocument(){
                if(aux.time != null && aux.wind != null){
                    publishData(aux);
                }
            }
            
            @Override
            public void startElement(String uri, String localName,String qName, Attributes attributes){
                stack.add(qName);
            }
            
            @Override
            public void endElement(String uri, String localName, String qName){
                stack.remove(stack.size()-1);
            }
            
            @Override
            public void characters(char ch[], int start, int length){
                if(stack.size() >= 3 && stack.get(2).equals("station")){
                    String str = String.copyValueOf(ch, start, length);
                    
                    if(stack.size() >= 4){
                        if(stack.get(3).equals("pubDate")){
                            try {
                                Date date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US).parse(str);
                                aux.time = new GregorianCalendar();
                                aux.time.setTime(date);
                            } catch (ParseException ex) {
                                System.out.println("Nope! " + ex);
                            }
                        }else if(stack.size() >= 6 && stack.get(3).equals("stationdata")){
                            parseStationData(str);
                        }
                    }
                }
            }
            
            // Pre: stack.size >= 6
            private void parseStationData(String str){
                String param = stack.get(4);
                String what = stack.get(5);
                
                if(what.equals("unit")){
                    unit = str;
                }else if(unit != null){
                    if(param.equals("temperature")){
                        if(what.equals("now")){
                            if(unit.equals("C"))
                                aux.temp = Globals.parseNumber(str);
                            else if(unit.equals("F"))
                                aux.temp = Globals.FtoC(str);
                            else
                                Globals.logError(getStationCode(), "MeteoclimaticStation: Falta interpretar " + unit, null);
                        }
                    }else if(param.equals("humidity")){
                        if(what.equals("now")){
                            if(unit.equals("%"))
                                aux.humidity = Globals.parseNumber(str);
                            else
                                Globals.logError(getStationCode(), "MeteoclimaticStation: Falta interpretar " + unit, null);
                        }
                    }else if(param.equals("barometre")){
                        if(what.equals("now")){
                            if(unit.equals("hPa") || unit.equals("mm") || unit.equals("bar"))
                                aux.barometer = Globals.parseNumber(str);
                            else
                                Globals.logError(getStationCode(), "MeteoclimaticStation: Falta interpretar " + unit, null);
                        }
                    }else if(param.equals("wind")){
                        if(what.equals("now")){
                            if(unit.equals("kmh"))
                                aux.wind = Globals.kmhToKnots(str);
                            else if(unit.equals("kts"))
                                aux.wind = Globals.parseNumber(str);
                            else
                                Globals.logError(getStationCode(), "MeteoclimaticStation: Falta interpretar " + unit, null);
                        }else if(what.equals("azimuth")){
                            aux.dir = Globals.parseNumber(str);
                        }
                    }else if(param.equals("rain")){
                        if(what.equals("total")){
                            if(unit.equals("mm"))
                                aux.rain = Globals.parseNumber(str);
                            else
                                Globals.logError(getStationCode(), "MeteoclimaticStation: Falta interpretar " + unit, null);
                        }
                    }
                }
            }
        };
        
        try {
            SAXParser parser = Globals.saxFactory.newSAXParser();
            
            String xml = Globals.getHTML("http://www.meteoclimatic.com/feed/xml/" + getMeteoclimaticID());
            if(xml != null)
                parser.parse(new ByteArrayInputStream(xml.getBytes()), handler);
        } catch (IOException ex) {
            Logger.getLogger(MeteoclimaticStation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(MeteoclimaticStation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(MeteoclimaticStation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
 
    
    
    protected abstract String getMeteoclimaticID();
    
}
