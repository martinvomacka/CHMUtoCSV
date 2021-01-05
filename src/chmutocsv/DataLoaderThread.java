package chmutocsv;

/**
 *
 * @author Martin Vomacka
 */
public class DataLoaderThread extends Thread{
    private final Stanice stanice;

    public DataLoaderThread(Stanice inStanice) {
        this.stanice = inStanice;
    }

    @Override
    public void run() {
        //System.out.println("http://hydro.chmi.cz/hpps/hpps_srzstationdyn.php?seq="+String.valueOf(stanice.getWebidStanice()));
        Updater web = new Updater();
        web.setURL("https://hydro.chmi.cz/hpps/hpps_srzstationdyn.php?seq="+String.valueOf(stanice.getWebidStanice()));
        web.readValues(stanice);
    }
}
