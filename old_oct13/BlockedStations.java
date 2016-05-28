/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lwlogger;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
 * La idea per detectar un repetit es el següent:
 * La funció check es la que 'ho fa tot'. Et diu si has de guardar o no la dada i el nombre de vegades que hauràs d'escriure *
 * A aquesta funcio se li passa la ultima dada guardada i la dada extreta al moment
 * Si son exactament iguals, llavors retornarà fals i s'apuntarà cuantes vegades està repetit (anirà incrementant fins a un maxim de N)
 * Si son diferents:
 *  Si ha estat repetida durant menys de N vegades, llavors d'alguna manera ha de notificar que cal escriure les ultimes N dades, perque 
 *      ni que estiguin repetides, considera que son correctes.
 *  Altrament, retorna true
 * 
 *  * -> 1 vol dir la dada actual. 2 vol dir la dada actual, més l'ultima dada. 3 vol dir la dada actual, més l'ultima dada dos vegades, etc...
 */

/**
 *
 * @author Victor
 */
public class BlockedStations {
    private static final String FILE = "blockedStations.dat";
    private static final int N = 3; // 15 minuts
    private static BlockedStations instance;
    private Map<String, Integer> repetitions;
    
    private BlockedStations(){
        repetitions = new HashMap<String, Integer>();
        
        load();
    }
    private static BlockedStations getInstance(){
        if(instance == null) instance = new BlockedStations();
        
        return instance;
    }
    
    public static int check(String code, Dada actual, Dada ultima){
        if(!actual.equals(ultima) || actual.wind.intValue() == 0){
            Integer n = getInstance().repetitions.get(code);
            getInstance().repetitions.remove(code);
            getInstance().save();
            
            if(n == null || n >= N){
                return 1;
            }
            
            return n+1;
        }else{
            Integer n = getInstance().repetitions.get(code);
            if(n == null){
                n = 1;
            }else{
                n = Math.min(n+1, N);
            }
            getInstance().repetitions.put(code, n);
            getInstance().save();
            
            return 0;
        }
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
                    String[] camps = line.split("\t");
                    String est = camps[0];
                    Integer rep = Integer.parseInt(camps[1]);
                    
                    repetitions.put(est, rep);
                }
            }
            
            br.close();
            fr.close();
        } catch (IOException ex) {
        }
    }
    private void save(){
        File f = new File(FILE);

        try {
            FileWriter fw = new FileWriter(f);
            
            Iterator<String> codis = repetitions.keySet().iterator();
            while(codis.hasNext()){
                String codi = codis.next();
                fw.write(codi + "\t" + repetitions.get(codi) + "\n");
            }
            
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
