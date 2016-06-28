package chmutocsv;

import java.util.LinkedList;

/**
 *
 * @author Martin Vomacka
 */
public class DataLoaderThread extends Thread{
    private final LinkedList<StaniceData> buffer;
    private final String jmenoStanice;
    private final String webidStanice;

    public DataLoaderThread(LinkedList<StaniceData> inBuffer, String inNazev, String inWebid) {
        this.buffer = inBuffer;
        this.jmenoStanice = inNazev;
        this.webidStanice = inWebid;
    }

    @Override
    public void run() {
        Updater web = new Updater();
        web.setURL("http://hydro.chmi.cz/hpps/hpps_srzstationdyn.php?seq="+webidStanice);
        StaniceData temp = new StaniceData(jmenoStanice);
        web.readValues(temp);
        buffer.add(temp);
    }
}
