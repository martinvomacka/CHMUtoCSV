package chmutocsv;

import java.util.ArrayList;
import java.util.HashMap;

public class Kraj {
    private final Integer webidKraje;
    private final HashMap<String, Integer> staniceKraje;
    
    public Kraj(Integer initWebid) {
        this.webidKraje=initWebid;
        this.staniceKraje = new HashMap<>();
    }
    
    public boolean vlozitStanici(String jmenoStanice, Integer webidStanice) {
        if(staniceKraje.containsKey(jmenoStanice))
            return false;
        else {
            staniceKraje.put(jmenoStanice, webidStanice);
            return true;
        }
    }
    
    public HashMap getSeznam() {
        return staniceKraje;
    }
    
    public void printStanice() {
        Object seznamNazvuStanic[] = staniceKraje.keySet().toArray();
        for (Object iterator : seznamNazvuStanic) {
            System.out.println("    Stanice: "+(String)iterator+" ID: "+staniceKraje.get((String)iterator));
        }
    }
    
    public int getStaniceWebid(String jmenoStanice) {
        return staniceKraje.get(jmenoStanice);
    }
    
    public int getPocetStanic() {
        return staniceKraje.size();
    }

    public Integer getWebidKraje() {
        return webidKraje;
    }
}
