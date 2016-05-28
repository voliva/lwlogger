/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.wunderground;

import lwlogger.meteoclimatic.*;
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
public abstract class WundergroundStation implements StationFetcher {
    private Dada ret = null;
    
    private void publishData(Dada data){
        ret = data;
    }
    
    @Override
    public Dada fetch() {
        
        DefaultHandler handler = new DefaultHandler(){
            String unit = null;
            Dada aux = null;
            
            @Override
            public void startDocument(){
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
                String val = attributes.getValue("val");
                if(qName.equals("dateutc")){
                    aux.time = Globals.parseCalendar(val, "yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"));
                }else if(qName.equals("windspeedmph")){
                    aux.wind = Globals.mphToKnots(Globals.parseNumber(val).floatValue());
                    if(aux.wind.intValue() < 0) aux.wind = null;
                }else if(qName.equals("windgustmph")){
                    aux.gust = Globals.mphToKnots(Globals.parseNumber(val).floatValue());
                    if(aux.gust.intValue() < 0) aux.gust = null;
                }else if(qName.equals("humidity")){
                    aux.humidity = Globals.parseNumber(val);
                    if(aux.humidity.intValue() < 0) aux.humidity = null;
                }else if(qName.equals("tempf")){
                    aux.temp = Globals.FtoC(Globals.parseNumber(val).floatValue());
                    if(aux.temp.intValue() < 0) aux.temp = null;
                }else if(qName.equals("rainin")){
                    aux.rain = Globals.parseNumber(val);
                    if(aux.rain.intValue() < 0) aux.rain = null;
                }else if(qName.equals("winddir")){
                    aux.dir = Globals.parseNumber(val);
                    if(aux.dir.intValue() < 0) aux.dir = null;
                }
            }
            
        };
        
        try {
            SAXParser parser = Globals.saxFactory.newSAXParser();
            
            String xml = Globals.getHTML("http://stationdata.wunderground.com/cgi-bin/stationlookup?station=" + getStationId());
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
 
    
    
    protected abstract String getStationId();
    
}
