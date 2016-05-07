/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author victor
 */
public class Dada {
    public Calendar time = null;
    public Number temp = null;
    public Number humidity = null;
    public Number barometer = null;
    public Number wind = null;
    public Number gust = null;
    public Number dir = null;
    public Number rain = null;
    
    public Dada(){
    }
    
    public boolean esCalma(){
        if(wind != null){
            if(wind.intValue() != 0) return false;
        }
        if(gust != null){
            if(gust.intValue() != 0) return false;
        }
        
        return true;
    }
    
    @Override
    public String toString(){
        String ret = "";
        
        DateFormat df = new SimpleDateFormat("HH:mm");
        NumberFormat nf = new DecimalFormat("####.#");
        
        if(time != null)
            ret += df.format(time.getTime());
        else
            ret += "-";
        ret += "\t";
        
        if(temp != null)
            ret += nf.format(temp.doubleValue());
        else
            ret += "-";
        ret += "\t";
        
        if(humidity != null)
            ret += humidity.intValue();
        else
            ret += "-";
        ret += "\t";
        
        if(barometer != null)
            ret += nf.format(barometer.doubleValue());
        else
            ret += "-";
        ret += "\t";
        
        if(wind != null)
            ret += nf.format(wind.doubleValue());
        else
            ret += "-";
        ret += "\t";
        
        if(gust != null)
            ret += nf.format(gust.doubleValue());
        else
            ret += "-";
        ret += "\t";
        
        if(dir != null)
            ret += dir.intValue();
        else
            ret += "-";
        ret += "\t";
        
        if(rain != null)
            ret += nf.format(rain.doubleValue());
        else
            ret += "-";
        
        return ret.replace(",", ".");
    }
    
    @Override
    public boolean equals(Object obj){
        if(!obj.getClass().equals(this.getClass())) return false;
        
        Dada d = (Dada)obj;
        
        if(d.barometer == null){
            if(this.barometer != null) return false;
        }else{
            if(this.barometer == null) return false;
            if(this.barometer.intValue() != d.barometer.intValue()) return false;
        }
        if(d.dir == null){
            if(this.dir != null) return false;
        }else{
            if(this.dir == null) return false;
            if(this.dir.intValue() != d.dir.intValue()) return false;
        }
        if(d.gust == null){
            if(this.gust != null) return false;
        }else{
            if(this.gust == null) return false;
            if(this.gust.intValue() != d.gust.intValue()) return false;
        }
        if(d.humidity == null){
            if(this.humidity != null) return false;
        }else{
            if(this.humidity == null) return false;
            if(this.humidity.intValue() != d.humidity.intValue()) return false;
        }
        if(d.rain == null){
            if(this.rain != null) return false;
        }else{
            if(this.rain == null) return false;
            if(this.rain.intValue() != d.rain.intValue()) return false;
        }
        if(d.temp == null){
            if(this.temp != null) return false;
        }else{
            if(this.temp == null) return false;
            if(this.temp.intValue() != d.temp.intValue()) return false;
        }
        if(d.wind == null){
            if(this.wind != null) return false;
        }else{
            if(this.wind == null) return false;
            if(this.wind.intValue() != d.wind.intValue()) return false;
        }
        
        return true;
    }
    
    public static Dada fromString(String str){
        return fromString(str, Calendar.getInstance());
    }
    public static Dada fromString(String str, Calendar day){
        try {
            Dada d = new Dada();
            String[] s = str.split("\t");
            
            if(day != null){
                String[] time = s[0].split(":");
                day.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                day.set(Calendar.MINUTE, Integer.parseInt(time[1]));
            }
            d.time = day;
            if(!s[1].equals("-"))
                d.temp = Float.parseFloat(s[1]);
            if(!s[2].equals("-"))
                d.humidity = Float.parseFloat(s[2]);
            if(!s[3].equals("-"))
                d.barometer = Float.parseFloat(s[3]);
            if(!s[4].equals("-"))
                d.wind = Float.parseFloat(s[4]);
            if(!s[5].equals("-"))
                d.gust = Float.parseFloat(s[5]);
            if(!s[6].equals("-"))
                d.dir = Float.parseFloat(s[6]);
            if(!s[7].equals("-"))
                d.rain = Float.parseFloat(s[7]);
            
            return d;
        }catch (Exception ex){
            return null;
        }
    }
}
