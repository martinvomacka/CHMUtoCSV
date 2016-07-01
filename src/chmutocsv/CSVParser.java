package chmutocsv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final LocalDate start;

    public CSVParser(LinkedList<Stanice> inSeznam, int inPocetDnu, int inOffsetFirst, String inFilename) {
        this.stanice = inSeznam;
        this.pocetDnu = inPocetDnu;
        this.offsetDnu = inOffsetFirst;
        this.filename = inFilename;
        this.easy = new SimpleDateFormat("dd.MM.yyyy");
        this.start = LocalDate.now().minusDays(6-inOffsetFirst);
        System.out.println(start.atStartOfDay());
    }
    
    public void printToConsole() {
        System.out.print("Datum;");
        for (Stanice stanice1 : stanice) {
            System.out.print(stanice1.getNazevStanice() + ";");
        }
        System.out.println();
        for (int i = 0; i <= pocetDnu; i++) {
            System.out.print(easy.format(Date.from(start.plusDays(i).atStartOfDay(ZoneId.systemDefault()).toInstant()))+";");
            for (Stanice stanice1 : stanice) {
                System.out.print(stanice1.getHodnoty().get(i+offsetDnu) + ";");
            }
            System.out.println();
        }
    }
    
    public void csvExport() {
        try
	{
            File file = new File(filename);
            if (file.exists()){
                file.delete();
            }
	    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),"CP1250");
            writer.append("Datum;");
            for (Stanice stanice1 : stanice) {
                writer.append(stanice1.getNazevStanice() + ";");
            }
	    writer.append('\n');
            for (int i = 0; i <= pocetDnu; i++) {
                writer.append(easy.format(Date.from(start.plusDays(i).atStartOfDay(ZoneId.systemDefault()).toInstant()))+";");
                for (Stanice stanice1 : stanice) {
                    writer.append(String.valueOf(stanice1.getHodnoty().get(i+offsetDnu)).replace('.', ',') + ";");
                }
                writer.append('\n');
            }
	    writer.flush();
	    writer.close();
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}