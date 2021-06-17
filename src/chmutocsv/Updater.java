package chmutocsv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Updater {
    private URL webpage;
    
    public boolean setURL(String plaintextURL) {
        try {
            this.webpage = new URL(plaintextURL);
            return true;
        } catch (MalformedURLException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public HashMap<String, Kraj> updateKraje() {
        try {
            BufferedReader webpagePlaintext = new BufferedReader(new InputStreamReader(webpage.openStream(), "UTF-8"));
            String inputLine;
            String jmenoKraje;
            int webidKraje=0;
            HashMap<String, Kraj> temp = new HashMap<>();
            while ((inputLine = webpagePlaintext.readLine()) != null) {
                if(inputLine.contains("select name=\"fkraj\"")) {
                    inputLine=inputLine.substring(108);
                    //System.out.println(inputLine);
                    for (int ptr=0; ptr<14; ptr++) {
                        inputLine=inputLine.substring(inputLine.indexOf("\"")+1);
                        //System.out.println(inputLine);
                        webidKraje=Integer.parseInt(inputLine.substring(0,inputLine.indexOf("\"")));
                        //System.out.println(webidKraje);
                        inputLine=inputLine.substring(inputLine.indexOf(">")+1);
                        jmenoKraje=inputLine.substring(0,inputLine.indexOf("<"));
                        //System.out.println(jmenoKraje);
                        temp.put(jmenoKraje, new Kraj(webidKraje));
                    }
                    continue;
                }
                if(inputLine.contains("select name=\"fkraj\""))
                    break;
            }
            webpagePlaintext.close();
            return temp;
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void naplnKraj(Kraj naplit) {
        try {
            BufferedReader webpagePlaintext = new BufferedReader(new InputStreamReader(webpage.openStream(), "UTF-8"));
            String inputLine;
            String jmenoStanice;
            Integer webidStanice;
            while ((inputLine = webpagePlaintext.readLine()) != null) {
                if(inputLine.contains("popUpWindow('hpps_srzstationdyn.php")) {
                    inputLine=inputLine.substring(inputLine.indexOf("seq=")+4);
                    webidStanice=Integer.decode(inputLine.substring(0, inputLine.indexOf("\'")));
                    inputLine=inputLine.substring(inputLine.indexOf(">")+1);
                    jmenoStanice=inputLine.substring(0, inputLine.indexOf("<"));
                    naplit.vlozitStanici(jmenoStanice, webidStanice);
                }
                if(inputLine.contains("Počet")) {
                    break;
                }
            }
            webpagePlaintext.close();
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public int isUpdateNeeded() {
        int actualValue=0;
        try {
            BufferedReader webpagePlaintext = new BufferedReader(new InputStreamReader(webpage.openStream(), "UTF-8"));
            String inputLine;
            while ((inputLine = webpagePlaintext.readLine()) != null) {
                if(inputLine.contains("Počet")) {
                    inputLine=inputLine.substring(inputLine.indexOf("záznamů")+15);
                    //System.out.println(inputLine);
                    actualValue=Integer.parseInt(inputLine.substring(0, inputLine.indexOf("<")));
                    break;
                }
            }
            webpagePlaintext.close();
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return actualValue;
    }
    
    public void readValues(Stanice stanice) {
        try {
            BufferedReader webpagePlaintext = new BufferedReader(new InputStreamReader(webpage.openStream(), "UTF-8"));
            String inputLine;
            int i=0;
            int daylimit=7;
            while ((inputLine = webpagePlaintext.readLine()) != null && daylimit>0) {
                if(inputLine.contains("td class=\"sdt\"")) {
                    i++;
                    if(i==2){
                        inputLine=inputLine.substring(inputLine.indexOf("\">")+2);
                        stanice.pushValue(Float.valueOf(inputLine.substring(0,inputLine.indexOf("<"))));
                        i=0;
                        daylimit--;
                    }
                }
            }
            webpagePlaintext.close();
        } catch (IOException ex) {
            Logger.getLogger(Updater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}