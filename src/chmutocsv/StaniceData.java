package chmutocsv;

import java.util.LinkedList;

public class StaniceData {
    private final String nazevStanice;
    private final Integer webidStanice;
    private final LinkedList<Float> hodnoty = new LinkedList<>();

    public StaniceData(String inNazev, Integer inWebid) {
        this.nazevStanice = inNazev;
        this.webidStanice = inWebid;
    }
    
    public void pushValue(float input) {
        this.hodnoty.add(input);
    }

    public String getNazevStanice() {
        return nazevStanice;
    }

    public Integer getWebidStanice() {
        return webidStanice;
    }

    public LinkedList<Float> getHodnoty() {
        return hodnoty;
    }
}
