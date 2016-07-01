package chmutocsv;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

/**
 *
 * @author Vomec
 */
public class CSVParser {
    private final LinkedList<Stanice> stanice;
    private final int pocetDnu;
    private final int offsetDnu;
    private final String filename;
    private final SimpleDateFormat easy;

    public CSVParser(LinkedList<Stanice> inSeznam, int inPocetDnu, int inOffsetToday, String inFilename) {
        this.stanice = inSeznam;
        this.pocetDnu = inPocetDnu;
        this.offsetDnu = inOffsetToday;
        this.filename = inFilename;
        this.easy = new SimpleDateFormat("dd.MM.yyyy");
    }
    
    public void print() {
        System.out.print("Datum;");
        for (Stanice stanice1 : stanice) {
            System.out.print(stanice1.getNazevStanice() + ";");
        }
        System.out.println();
        for (int i = 0; i < pocetDnu; i++) {
            System.out.print(easy.format(Date.from(LocalDate.now().minusDays(pocetDnu-offsetDnu-i-1).atStartOfDay(ZoneId.systemDefault()).toInstant()))+";");
            for (Stanice stanice1 : stanice) {
                System.out.print(stanice1.getHodnoty().get(pocetDnu-offsetDnu-i-1) + ";");
            }
            System.out.println();
        }
    }
}