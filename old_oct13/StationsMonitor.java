/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger;

import java.io.*;
import java.util.*;

/*
 * La idea d'aquesta classe-fitxer es mantenir una llista d'estacions per poder
 * detectar repetits i, de pas, si es una estacio fallada.
 * El format del fitxer es nom_est ult_dada n_rep falla
 */

/**
 *
 * @author Victor
 */
public class StationsMonitor {
    private static final String FILE = "stationsmonitor.dat";
    private static final int N = 3; // 15 minuts
    private static final int N_CALMA = 48; // 4 hores
    private static StationsMonitor instance;
    
    private static class StationInfo {
        public Dada ultimaDada;
        public int repeticions; // Mínim: 0 repeticions
        public boolean falla;
        
        @Override
        public String toString(){
            String ret = "";
            if(ultimaDada != null) ret = ultimaDada.toString();
            return ret + "\\" + repeticions + "\\" + Boolean.toString(falla);
        }
    }
    
    private Map<String, StationInfo> mapeig;
    
    private StationsMonitor(){
        mapeig = new HashMap<String, StationInfo>();
        
        load();
    }
    private static StationsMonitor getInstance(){
        if(instance == null) instance = new StationsMonitor();
        
        return instance;
    }
    
    public static Dada getLastData(String code){
        StationInfo inf = getInstance().mapeig.get(code);
        if(inf == null) return null;
        return inf.ultimaDada;
    }
    public static int check(String code, Dada actual){
        try {
            StationInfo station;
            station = getInstance().mapeig.get(code);
            if(station == null){
                station = getInstance().createSI(code);
                station.ultimaDada = actual;

                return 1;
            }

            Dada ultima = station.ultimaDada;
            int n_rep = station.repeticions;
            boolean falla = station.falla;

            int ret;
            if(ultima == null || ultima.wind == null){
                ret = 1;
                n_rep = 0;
                falla = false;
            }else{
                if(Globals.getTime(actual.time) - 15 > Globals.getTime(Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))) // Si està al futur
                    || Globals.getTime(actual.time) < Globals.getTime(ultima.time)){ // o si esta al passat
                    ret = 0;
                }else if(actual.equals(ultima)){
                    n_rep++;

                    if(actual.esCalma()){ // Calma
                        if(n_rep >= N_CALMA){
                            n_rep = N_CALMA;
                            falla = true;
                        }
                    }else{
                        if(n_rep >= N){
                            n_rep = N;
                            falla = true;
                        }
                    }

                    ret = 0;
                }else{
                    falla = false;

                    if(ultima.wind.intValue() == 0){
                        if(n_rep >= N_CALMA){
                            ret = 1;
                        }else{
                            ret = n_rep+1;
                        }
                    }else{
                        if(n_rep >= N){
                            ret = 1;
                        }else{
                            ret = n_rep+1;
                        }
                    }
                    n_rep = 0;
                }
            }

            station.falla = falla;
            station.repeticions = n_rep;
            station.ultimaDada = actual;

            return ret;
        }catch(Exception ex){
            ex.printStackTrace(System.err);
            return 1;
        }
    }
    public static void setFalla(String code){
        StationInfo station;
        station = getInstance().mapeig.get(code);
        if(station == null){
            station = getInstance().createSI(code);
            station.falla = true;
            return;
        }
        
        station.falla = true;
    }
    
    private synchronized StationInfo createSI(String code){
        StationInfo station = new StationInfo();
        station.ultimaDada = null;
        station.falla = false;
        station.repeticions = 0;
        
        getInstance().mapeig.put(code, station);
        
        return station;
    }
    private void load(){
        File f = new File(FILE);
        
        FileReader fr;
        try {
            fr = new FileReader(f);
        } catch (FileNotFoundException ex) {
            return;
        }
        BufferedReader br = new BufferedReader(fr);
        String line = "";

        try {
            while(line != null) {
                line = br.readLine();
                if(line != null){
                    String[] camps = line.split("\\\\");
                    String est = camps[0];
                    StationInfo stInf = new StationInfo();
                    stInf.ultimaDada = Dada.fromString(camps[1]);
                    stInf.repeticions = Integer.parseInt(camps[2]);
                    stInf.falla = Boolean.parseBoolean(camps[3]);
                    
                    mapeig.put(est, stInf);
                }
            }
            
            br.close();
            fr.close();
        } catch (IOException ex) {
        }
    }
    public static void save(){
        getInstance()._save();
    }
    private void _save(){
        File f = new File(FILE);

        try {
            FileWriter fw = new FileWriter(f);
            
            Iterator<String> codis = mapeig.keySet().iterator();
            while(codis.hasNext()){
                String codi = codis.next();
                fw.write(codi + "\\" + mapeig.get(codi).toString() + "\n");
            }
            
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
