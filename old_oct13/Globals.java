/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.imageio.ImageIO;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author victor
 */
public class Globals {
    public static int readTimeout = 30000;
    public static int connectionTimeout = 10000;
    public static SAXParserFactory saxFactory = SAXParserFactory.newInstance();
    
    //public static String getHTML(String url, boolean timeout, Map<String, String> headers){
    public static String getHTML(String url, Map<String, String> headers){
        try {
            URL _url = new URL(url);
            
            URLConnection con = _url.openConnection();
            //if(timeout){
                con.setConnectTimeout(connectionTimeout);
                con.setReadTimeout(readTimeout);
            //}
            if(headers != null){
                Iterator it = headers.entrySet().iterator();
                while(it.hasNext()){
                    Map.Entry<String,String> pairs = (Map.Entry<String,String>)it.next();
                    con.setRequestProperty(pairs.getKey(), pairs.getValue());
                }
            }
            
            BufferedReader bf = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            
            char[] cb = new char[1024];
            
            String ret = "";
            int read;
            while ((read = bf.read(cb)) > 0){
                ret = ret + String.valueOf(cb, 0, read);
            }
            
            bf.close();
            
            return ret;
        } catch (SocketTimeoutException ex) {
        } catch (ConnectException ex) {
        } catch (UnknownHostException ex) { // DNS fail
        } catch (IOException ex){
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    /*public static String getHTML(String url, boolean timeout){
        return getHTML(url, timeout, null);
    }*/
    public static String getHTML(String url){
        //return getHTML(url, true);
        return getHTML(url, null);
    }
    
    public static String postHTML(String url, String parameters){
        try {
            URL _url = new URL(url);
            
            URLConnection con = _url.openConnection();
            //if(timeout){
                con.setConnectTimeout(connectionTimeout);
                con.setReadTimeout(readTimeout);
            //}           
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
            con.setRequestProperty("charset", "utf-8");
            con.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            con.setUseCaches (false);
            
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(parameters);
            wr.flush();
            wr.close(); // ?
            
            BufferedReader bf = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            
            char[] cb = new char[1024];
            
            String ret = "";
            int read;
            while ((read = bf.read(cb)) > 0){
                ret = ret + String.valueOf(cb, 0, read);
            }
            
            bf.close();
            
            
            return ret;
        } catch (SocketTimeoutException ex) {
        } catch (ConnectException ex) {
        } catch (UnknownHostException ex) { // DNS fail
        } catch (IOException ex){
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    public static String postHTML(String url){
        return postHTML(url, "");
    }
    
    public static BufferedImage getImage(String url, boolean timeout){
        try {
            URL _url = new URL(url);
            
            URLConnection con = _url.openConnection();
            if(timeout){
                con.setConnectTimeout(connectionTimeout);
                con.setReadTimeout(readTimeout);
            }
            
            try {
                BufferedImage bimg = ImageIO.read(con.getInputStream());

                return bimg;
            }catch (EOFException ex){
            }
        } catch (Exception ex) {
        }
        return null;
    }
    public static BufferedImage getImage(String url){
        return getImage(url, true);
    }
    
    public static void logError(String estacio, String info, Exception exception){
        File f = new File("error.log");

        try {
            PrintWriter fw = new PrintWriter(new FileWriter(f, true));
            
            DateFormat df = new SimpleDateFormat("HH:mm");
            
            String excepcio = "No Exception";
            if(exception != null)
                excepcio = exception.getMessage();
            
            fw.write(
                    df.format(Calendar.getInstance().getTime()) + " Estacio: " + estacio  + "\n" +
                    "/////Info////\n" + 
                    info + "\n" +
                    "/////Fi Info////\n" +
                    "Exception: " + excepcio + "\n");
            if(exception != null)
                exception.printStackTrace(fw);
            fw.write("\nEND ERROR\n\n");
            
            fw.close();
            
            System.out.println("Error logged. Check error.log");
        } catch (IOException ex) {
            System.err.println("Can't write to log file:");
            ex.printStackTrace(System.err);
            System.err.println("Original error to be logged:");
            ex.printStackTrace(System.err);
        }
    }
    
    public static Calendar parseCalendar(String source, String format){
        return parseCalendar(source, format, TimeZone.getTimeZone("Europe/Madrid"));
    }
    public static Calendar parseCalendar(String source, String format, TimeZone timezone){
        // 2013-09-24 09:01:21 => yyyy-MM-dd HH:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(timezone);
        try {
            Calendar ret = new GregorianCalendar();
            ret.setTime(sdf.parse(source));
            

            return ret;
        } catch (ParseException ex) {
            return null;
        }
    }
    public static Calendar parseCalendar(String year, String month, String day, String hour, String minute){
        if(year.length() == 2){
            year = "20" + year;
        }
        
        Calendar ret = new GregorianCalendar(); // (Calendar)Calendar.getInstance().clone();
        ret.set(
                Integer.parseInt(year),
                Integer.parseInt(month) - 1,
                Integer.parseInt(day),
                Integer.parseInt(hour),
                Integer.parseInt(minute));
        return ret;
    }
    public static Calendar parseCalendar(TimeZone tz, String year, String month, String day, String hour, String minute){ // TODO
        return Calendar.getInstance();
    }
    public static Number parseNumber(Globals.Splitter sp){
        return parseNumber(sp.getString());
    }
    public static Number parseNumber(String str){
        if(str == null) return null;
        if(str.equals("")) return null;
        if(str.equals(" ")) return null;
        if(str.equals("-")) return null;
        if(str.matches(".*[a-zA-Z]+.*")) return null;
        try{
            return Float.parseFloat(str);
        } catch (NumberFormatException ex){
            ex.printStackTrace(System.err);
            return null;
        }
    }
    
    public static Float kmhToKnots(Float kmh){
        return (float)(kmh / 1.852);
    }
    public static Float kmhToKnots(String kmh){
        Number n = parseNumber(kmh);
        if(n == null) return null;
        return kmhToKnots(n.floatValue());
    }
    
    public static Float mpsToKnots(Float mps){
        return (float)(mps * 1.94384449);
    }
    public static Float mpsToKnots(String mps){
        Number n = parseNumber(mps);
        if(n == null) return null;
        return mpsToKnots(n.floatValue());
    }
    
    public static Float mphToKnots(Float mph){
        return (float)(mph * 0.8689);
    }
    public static Float mphToKnots(String mph){
        Number n = parseNumber(mph);
        if(n == null) return null;
        return mphToKnots(n.floatValue());
    }
    
    public static Float inhgTombar(Float inHg){
        return (float)(inHg * 33.8639);
    }
    public static Float inhgTombar(String inHg){
        Number n = parseNumber(inHg);
        if(n == null) return null;
        return inhgTombar(n.floatValue());
    }
    
    public static Float inTomm(Float in){
        return (float)(in * 25.4);
    }
    public static Float inTomm(String in){
        Number n = parseNumber(in);
        if(n == null) return null;
        return inTomm(n.floatValue());
    }
    
    public static Float FtoC(Float f){
        return (f - 32) * 5 / 9;
    }
    public static Float FtoC(String f){
        Number n = parseNumber(f);
        if(n == null) return null;
        return FtoC(n.floatValue());
    }
    
    /**
     * 
     * @param c
     * @return Minut del dia
     */
    public static int getTime(Calendar c){
        return c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
    }
    
    public static Integer txtToDir(String txt){
        txt = txt.trim();
        txt = txt.replace("O", "W");
	if(txt.equals("N") || txt.equals("Norte")) return 0;
	if(txt.equals("NNE")) return 23;
	if(txt.equals("NE") || txt.equals("Noreste")) return 45;
	if(txt.equals("ENE")) return 68;
	if(txt.equals("E") || txt.equals("Este")) return 90;
	if(txt.equals("ESE")) return 113;
	if(txt.equals("SE") || txt.equals("Sureste")) return 135;
	if(txt.equals("SSE")) return 158;
	if(txt.equals("SES")) return 158;
	if(txt.equals("S") || txt.equals("Sur")) return 180;
	if(txt.equals("SSW")) return 203;
	if(txt.equals("SW") || txt.equals("Suroeste")) return 225;
	if(txt.equals("WSW")) return 248;
	if(txt.equals("W") || txt.equals("Oeste")) return 270;
	if(txt.equals("WNW")) return 293;
	if(txt.equals("NW") || txt.equals("Noroeste")) return 315;
	if(txt.equals("NNW")) return 338;
        
        return null;
    }
    public static Integer nomMesEspToNum(String mes){
	if(mes.equals("enero")) return 1;
	if(mes.equals("febrero")) return 2;
	if(mes.equals("marzo")) return 3;
	if(mes.equals("abril")) return 4;
	if(mes.equals("mayo")) return 5;
	if(mes.equals("junio")) return 6;
	if(mes.equals("julio")) return 7;
	if(mes.equals("agosto")) return 8;
	if(mes.equals("septiembre")) return 9;
	if(mes.equals("octubre")) return 10;
	if(mes.equals("noviembre")) return 11;
	if(mes.equals("diciembre")) return 12;
        return null;
    }
    
    public static class Splitter{
        private String str;
        
        public Splitter(String str){
            this.str = str;
        }
        
        public Splitter getToStrEx(String find){
            if(str.contains(find))
                str = str.substring(0, str.indexOf(find));
            else
                str = "";
            return this;
        }
        public Splitter getToStr(String find){
            if(str.contains(find))
                str = str.substring(0, str.indexOf(find) + find.length());
            else
                str = "";
            return this;
        }
        public Splitter cropToStr(String find){
            if(str.contains(find))
                str = str.substring(str.indexOf(find));
            else
                str = "";
            return this;
        }
        public Splitter cropToStrEx(String find){
            cropToStr(find);
            if(str.length() < find.length())
                str = "";
            else
                str = str.substring(find.length());
            return this;
        }
        public String getString(){
            return str;
        }
    }
}
