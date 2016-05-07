/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import lwlogger.aemet.AeroportBCN;
import lwlogger.aemet.Blanes;
import lwlogger.aemet.SantJaumeEnveja;
import lwlogger.aemet.SantaSusana;
import lwlogger.datawind.CMSitges;
import lwlogger.datawind.CMV_Barcelona;
import lwlogger.datawind.CNMasnou;
import lwlogger.davis.*;
import lwlogger.independent.*;
import lwlogger.meteoclimatic.MeteoPremia;
import lwlogger.meteoclimatic.PortLlanca;
import lwlogger.meteoclimatic.StFeliuGuixols;
import lwlogger.weatherdisplay.*;
import lwlogger.weatherflash.*;
import lwlogger.weatherlink.*;
import lwlogger.windsup.Riumar;
import lwlogger.windsup.Trabucador;
import lwlogger.wunderground.WunderCabrera;
import lwlogger.davis.Cadiz;
import lwlogger.wunderground.WunderLlanca;
import lwlogger.wunderground.WunderPratLlobregat;
import lwlogger.wunderground.WunderRoses;
import lwlogger.wunderground.WunderTorrevieja;
import lwlogger.xema.*;

/**
 *
 * @author victor
 */
public class LWLogger {
    private static final boolean WRITE_CONSOLE = false;
    private static boolean SHOW_TIME;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        boolean THREADED = true;
        SHOW_TIME = false;
                
        String stationDebug = null;
        for(int i=0; i<args.length; i++){
            if(args[i].equals("-nothread"))
                THREADED = false;
            else if(args[i].equals("-t"))
                SHOW_TIME = true;
            else if(args[i].equals("--help")){
                showCommands();
                return;
            }
            else if(args[i].equals("-station")){
                i++;
                if(i < args.length){
                    stationDebug = args[i];
                }
            }
        }
        
        
        long startMilis = Calendar.getInstance().getTimeInMillis();
        
        ArrayList<StationFetcher> stations = new ArrayList<StationFetcher>();
        
        stations.add(new CMTorredembarra());
        stations.add(new CNEstartit());
        stations.add(new CMAltafulla());
        stations.add(new CMSitges());
        stations.add(new CMV_Barcelona());
        stations.add(new CNBasetes());
        stations.add(new CNEBalis());
        stations.add(new CNGarraf());
        stations.add(new CNLEscala());
        stations.add(new CNMasnou());
        stations.add(new CNOropesa());
        stations.add(new CNPortAro());
        stations.add(new CVBenicassim());
        stations.add(new Deveses());
        stations.add(new CWSantaPola());
        stations.add(new RCNCalpe());
        stations.add(new RCNGandia());
        stations.add(new CNSPoca());
        stations.add(new CNVilanova());
        stations.add(new GENRoses());
        stations.add(new KastasWind());
        stations.add(new MetCambrils());
        stations.add(new CNHospitaletVandellos());
        stations.add(new CNVilassarMar());
        stations.add(new PontPetroli());
        stations.add(new CVPAndratx());
        stations.add(new CNCostaBrava());
        //stations.add(new CNPortSelva()); Mentre no funcioni -> timeout de 30 segons!
        stations.add(new CNGallineras());
        stations.add(new CNVitoria());
        stations.add(new RCAsturRegatas());
        stations.add(new CVCanetMar()); 
        stations.add(new SantaSusana());
        stations.add(new Blanes());
        stations.add(new SantJaumeEnveja());
        stations.add(new Oliva());
        stations.add(new Denia());
        stations.add(new Piles());
        stations.add(new Altea());
        stations.add(new AeroportBCN());
        stations.add(new Saler());
        stations.add(new PortSitges());
        stations.add(new Empuriabrava());
        stations.add(new MrCadaques());
        stations.add(new WindOliva());
        stations.add(new MeteoCullera());
        stations.add(new PinedaMar());
        // stations.add(new CVCanet()); // versio meteoclimatic. Funciona millor la versio de WeatherLinkStation
        stations.add(new MeteoPremia());
        stations.add(new PortLlanca());
        stations.add(new StFeliuGuixols());
        stations.add(new _17nudos());
        stations.add(new MalgratMar());// Nous
        stations.add(new StPerePescadorX());
        stations.add(new StPerePescador());
        stations.add(new Alcanar());
        stations.add(new Amposta());
        stations.add(new PratLlobregat());
        stations.add(new SantJaumeEnvejaX());
        stations.add(new StCarlesRapita());
        stations.add(new Tarragona());
        stations.add(new Viladecans());
        stations.add(new Riumar());
        stations.add(new Trabucador());
        stations.add(new PortLeucate());
        stations.add(new MeteoXabia()); // Permis concedit toni@meteoxabia.com
        stations.add(new WunderTorrevieja()); // Permis concedit Facebook TODO Migrar
        stations.add(new WunderCabrera()); // Permis concedit hotmail ciezar@gmail.com TODO Migrar
        stations.add(new WunderRoses()); // TODO Migrar
        stations.add(new Cadiz()); // Permis concedit v.oliva.v Pedro Javier Alvarez
        stations.add(new Marbella()); // Permis ongoing v.oliva.v adolfomendez1@gmail.com
        //stations.add(new TestStation());
        
        if(stationDebug == null){
            if(THREADED){
                ArrayList<FetchThread> threads = new ArrayList<FetchThread>();
                for(int i=0; i<stations.size(); i++){
                    FetchThread ft = new FetchThread(stations.get(i));
                    threads.add(ft);
                    ft.start();
                }
                for(int i=0; i<threads.size(); i++){
                    try {
                        threads.get(i).join();
                    } catch (InterruptedException ex) {
                    }
                }
            }else{
                for(int i=0; i<stations.size(); i++){
                    String c = stations.get(i).getStationCode();
                    try {
                        Dada d = stations.get(i).fetch();

                        writeDada(d, stations.get(i).getStationCode());
                    }catch (Exception ex){
                        ex.printStackTrace(System.err);
                    }
                }
            }
            
            StationsMonitor.save();
        }else{
            for(int i=0; i<stations.size(); i++){
                String c = stations.get(i).getStationCode();
                if(c.equals(stationDebug)){
                    try {
                        Dada d = stations.get(i).fetch();

                        System.out.println(stations.get(i).getStationCode() + ": " + d);
                        writeDada(d, stations.get(i).getStationCode());
                    }catch (Exception ex){
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }
        
        if(SHOW_TIME){
            long endMilis = Calendar.getInstance().getTimeInMillis();
            System.out.println("Runtime: " + (endMilis - startMilis) + "ms");
        }
    }

    private static void showCommands() {
        System.out.println("-nothread Don't use threads");
        System.out.println("-t Show execution time (in ms)");
        System.out.println("-station code Show only data from Station whose code is code");
    }
    
    private static class FetchThread extends Thread {
        private StationFetcher sf;
        
        public FetchThread(StationFetcher sf){
            this.sf = sf;
        }
        
        @Override
        public void run(){
            try {
                long startMilis = Calendar.getInstance().getTimeInMillis();
                
                Dada d = sf.fetch();

                writeDada(d, sf.getStationCode());
                
                if(SHOW_TIME){
                    long endMilis = Calendar.getInstance().getTimeInMillis();
                    System.out.println(sf.getStationCode() + ": " + (endMilis - startMilis) + "ms");
                }
            } catch (Exception ex){
                ex.printStackTrace(System.err);
            }
        }
    }
    
    private synchronized static void writeDada(Dada d, String codiEst){
        if(d == null || d.time == null){
            StationsMonitor.setFalla(codiEst);
            return;
        }
        
        if(WRITE_CONSOLE){
            System.out.println(codiEst + ": " + d);
            return;
        }
        
        DateFormat df = new SimpleDateFormat("yyyy_MM_dd");
             
        File dir = new File("www/" + codiEst + "/");
        if(!dir.exists()){
            dir.mkdirs();
        }
        
        File f = new File(dir, df.format(d.time.getTime()) + ".dat");

        Dada lastData = StationsMonitor.getLastData(codiEst);
        if(lastData != null){
            int lastTime = Globals.getTime(lastData.time);
            int time = Globals.getTime(d.time);

            if(lastTime == time){
                StationsMonitor.setFalla(codiEst);
                return;
            }
        }

        //int n = BlockedStations.check(codiEst, d, lastData);
        int n = StationsMonitor.check(codiEst, d);
        if(n == 0) return;

        if(n > 1 && lastData != null){ // Tot i que lastData mai serà nul si n > 1, pero por si acaso
            n--;
            for(int i=0; i<n; i++){
                writeLine(f, lastData.toString());
                
                lastData.time.setTimeInMillis(lastData.time.getTimeInMillis() + 5 * 60 * 1000);
            }
        }

        writeLine(f, d.toString());
    }
    
    private static synchronized void writeLine(File f, String str){
        try {
            FileWriter fw = new FileWriter(f, true);
            
            fw.write(str + "\n");
            
            fw.close();
        } catch (IOException ex) {
            System.err.println("Can't write to file " + f.getAbsolutePath());
            System.err.println(str);
        }
    }
    
    private static Dada getLastData(File f) throws FileNotFoundException{
        // Pre: el fitxer ja existeix
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String line = "";
        ArrayList<String> lines = new ArrayList<String>();

        try {
            while(line != null) {
                line = br.readLine();
                if(line != null)
                    lines.add(line);
            }
            
            br.close();
            fr.close();
            
            if(lines.size() <= 0) return null;
            line = lines.get(lines.size()-1);
            
            return Dada.fromString(line);
        } catch (IOException ex) {
        }
        
        return null;
    }
}
