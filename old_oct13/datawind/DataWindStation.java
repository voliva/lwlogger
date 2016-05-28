/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger.datawind;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import lwlogger.Dada;
import lwlogger.Globals;
import lwlogger.StationFetcher;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author victor
 */
public abstract class DataWindStation implements StationFetcher {
    @Override
    public abstract Dada fetch();

    @Override
    public abstract String getStationCode();
    
    protected Dada fetchFromImg(String url){
        BufferedImage img = Globals.getImage(url);
        if(img == null) return null;
        
        OCR ocr = new OCR(img);
        if(ocr.isRed(402, 57)) return null;
        
        Dada d = new Dada();
        d.time = Calendar.getInstance();
        d.dir = ocr.recognizeNatural(653, 100, 3);
        d.wind = ocr.recognizeDecimal(653, 100+57);
        d.gust = ocr.recognizeDecimal(653, 100+57+57);
        
        if(d.temp.intValue() == -22 && d.wind.intValue() == 37) return null;
        
        return d;
    }
    
    private Dada ret;
    protected Dada fetchFromBook(String url){
        try {
            /* like http://www.datawind.es/fcv/cma/book/php/lastdata.php:
    <xml>
            <wasp>
                    <id>2012-08-14 23:59:01</id>
                    <counter>2012-08-14 23:59:</counter>
                    <ohum>88</ohum>
                    <ihum>69</ihum>
                    <otemp>26.5</otemp>
                    <itemp>29.4</itemp>
                    <dew>24.3</dew>
                    <chill>26.5</chill>
                    <hpa>1,011.2</hpa>
                    <sea>1,013.2</sea>
                    <vane>135</vane>
                    <speed>4.4</speed>
                    <gust>5.1</gust>
            </wasp>
    </xml>
    */
            
            String xml = Globals.getHTML(url);
            if(xml == null) return null;
            
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            
            ret = new Dada();
            DefaultHandler handler = new DefaultHandler(){
                private String currentElement = "";
                
                @Override
                public void startElement(String uri, String localName,String qName, 
                        Attributes attributes) throws SAXException {
                    currentElement = qName;
                }
                
                @Override
                public void endElement(String uri, String localName,
                        String qName) throws SAXException {
                    currentElement = "";
                }
                
                @Override
                public void characters(char ch[], int start, int length) throws SAXException {
                    String str = new String(ch, start, length);
                    if(currentElement.equals("id")){
                        String[] id = str.split(" ");
                        if(id.length < 2) return;
                        String[] date = id[0].split("-");
                        String[] time = id[1].split(":");
                        if(date.length < 3) return;
                        if(time.length < 2) return;
                        
                        ret.time = Globals.parseCalendar(date[0], date[1], date[2], time[0], time[1]);
                    }else if(currentElement.equals("ohum")){
                        ret.humidity = Integer.parseInt(str);
                    }else if(currentElement.equals("otemp")){
                        ret.temp = Float.parseFloat(str);
                    }else if(currentElement.equals("sea")){
                        str = str.replace(",", "");
                        ret.barometer = Float.parseFloat(str);
                    }else if(currentElement.equals("vane")){
                        ret.dir = Float.parseFloat(str);
                    }else if(currentElement.equals("speed")){
                        ret.wind = Globals.mpsToKnots(Float.parseFloat(str));
                    }else if(currentElement.equals("gust")){
                        ret.gust = Globals.mpsToKnots(Float.parseFloat(str));
                    }
                }
            };
            
            parser.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")), handler);
            
            if(ret.time != null)
                return ret;
        } catch (IOException ex) {
        } catch (ParserConfigurationException ex) {
        } catch (SAXException ex) {
        }
        return null;
    }
    protected Dada fetchFromXML(String url){
        try {
            String xml = Globals.getHTML(url);
            
            // Com que el xml té errors, no podem fer servir SAX
            Globals.Splitter gs;
            
            ret = new Dada();
            ret.time = Calendar.getInstance();

            gs = new Globals.Splitter(xml);
            gs.cropToStr("id=th0")
                    .cropToStr("temp")
                    .cropToStrEx("=")
                    .getToStrEx(" ");
            ret.temp = Float.parseFloat(gs.getString());
            
            gs = new Globals.Splitter(xml);
            gs.cropToStr("id=thb0")
                    .cropToStrEx("seapress=")
                    .getToStrEx("/>");
            ret.barometer = Float.parseFloat(gs.getString());
            
            gs = new Globals.Splitter(xml);
            gs.cropToStr("id=wind0")
                    .cropToStrEx("dir=")
                    .getToStrEx(" ");
            ret.dir = Integer.parseInt(gs.getString());
            
            gs = new Globals.Splitter(xml);
            gs.cropToStr("id=wind0")
                    .cropToStrEx("ms=")
                    .getToStrEx(" ");
            ret.wind = Globals.mpsToKnots(Float.parseFloat(gs.getString()));
            
            gs = new Globals.Splitter(xml);
            gs.cropToStr("id=wind0")
                    .cropToStrEx("gust=")
                    .getToStrEx(" ");
            ret.gust = Globals.mpsToKnots(Float.parseFloat(gs.getString()));
            
            gs = new Globals.Splitter(xml);
            gs.cropToStr("id=rain0")
                    .cropToStrEx("rate=")
                    .getToStrEx(" ");
            ret.rain = Float.parseFloat(gs.getString());
            
            if(ret.barometer.intValue() >= 3600) return null;
            if(ret.temp.intValue() < -100) return null;
            
            return ret;
        } catch (Exception ex) {
        }
        return null;
    }
    
    private static class OCR {
        /**
        DataWindStation OCR v1

        Dir: 653 100

        Mida caracter 32 41
        Separacio 37 57
        separacio coma 57

        zero 29 11
        un 15 9
        dos 1 8
        quatre 11 10
        cinc 11 15
        set 1 1
        vuit 29 32
        nou 1 10
        sis 19 1 --- second round
        tres 3 2 --- third round


        ** Curiosament, el zero decimal esta un pixel m�s a l'esquerre. En aquest cas, aix� queda aixi:

        zero 0 13		!!
        un 15 9
        dos 1 8
        quatre 11 10
        cinc 11 15
        set 1 1
        vuit 28 8		!!
        nou 29 13		!!
        sis 19 1 --- second round
        tres 3 2 --- third round
        */
        
        private final BufferedImage img;
        
        public OCR(BufferedImage img){
            this.img = img;
        }
 
        public double recognizeDecimal(int x, int y){
            double ret = 0;
            ret = (double)recognizeNatural(x, y, 1, true) / (double)10;
            ret += recognizeNatural(x - 57, y, 2, false);
            return ret;
        }
        
        public int recognizeNatural(int x, int y, int max){
            return recognizeNatural(x, y, max, false);
        }
        private int recognizeNatural(int x, int y, int max, boolean decimal){
            int ret = 0;
            int n=1;
            
            for(int i=0; i<max; i++){
                if(fits1(x, y))
                    ret += 1 * n;
                else if(fits2(x, y))
                    ret += 2 * n;
                else if(fits4(x, y))
                    ret += 4 * n;
                else if(fits5(x, y))
                    ret += 5 * n;
                else if(fits7(x, y))
                    ret += 7 * n;
                else if(fits8(x, y, decimal))
                    ret += 8 * n;
                else if(fits9(x, y, decimal))
                    ret += 9 * n;
                else if(fits0(x, y, decimal))
                    ret += 0 * n;
                else if(fits6(x, y))
                    ret += 6 * n;
                else if(fits3(x, y))
                    ret += 3 * n;
                
                x = x-37;
                n = n*10;
            }
            return ret;
        }
        
        public boolean isRed(int x, int y){
            return img.getRGB(x, y) == 0xFFFF0000;
        }
        private boolean isPaleBlue(int x, int y){
            return img.getRGB(x, y) == 0xFF7777AA;
        }
        
        private boolean fits0(int x, int y, boolean decimal){
            if(decimal)
                return isPaleBlue(x, y+13);
            else
                return isPaleBlue(x+29, y+11);
        }
        private boolean fits1(int x, int y){
            return isPaleBlue(x+15, y+9);
        }
        private boolean fits2(int x, int y){
            return isPaleBlue(x+1, y+8);
        }
        private boolean fits3(int x, int y){
            return isPaleBlue(x+3, y+2);
        }
        private boolean fits4(int x, int y){
            return isPaleBlue(x+11, y+10);
        }
        private boolean fits5(int x, int y){
            return isPaleBlue(x+11, y+15);
        }
        private boolean fits6(int x, int y){
            return isPaleBlue(x+19, y+1);
        }
        private boolean fits7(int x, int y){
            return isPaleBlue(x+1, y+1);
        }
        private boolean fits8(int x, int y, boolean decimal){
            if(decimal)
                return isPaleBlue(x+28, y+8);
            else
                return isPaleBlue(x+29, y+32);
        }
        private boolean fits9(int x, int y, boolean decimal){
            if(decimal)
                return isPaleBlue(x+29, y+13);
            else
                return isPaleBlue(x+1, y+10);
        }
    }
}
