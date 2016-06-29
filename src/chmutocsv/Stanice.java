package chmutocsv;

import java.util.LinkedList;

/**
 * Třída obstarávající funkcionalitu na úrovni jedné stanice. Uchovává název
 * stanice a seznam načtených srážkových hodnot pro tuto stanici (maximálně však
 * 7 - více jich web ČHMÚ neuchovává).
 * <p>
 * @author Martin Vomáčka
 */
public class Stanice {

    /**
     * Název stanice pro kterou jsou načítána data.
     */
    private final String nazevStanice;
    /**
     * Název stanice pro kterou jsou načítána data.
     */
    private final Integer webidStanice;
    /**
     * Název kraje pod který stanice spadá.
     */
    private final Integer webidKraje;
    /**
     * Zřetězený seznam načtených srážkových hodnot.
     */
    private final LinkedList<Float> hodnoty;

    /**
     * Konstruktor třídy, povinná je specifikace názvu stanice. Provede
     * inicializace zřetězeného seznamu (prázdný seznam).
     * <p>
     * @param inNazev Název danné stanice
     * @param inWebidStanice Webid danné stanice
     * @param inWebidKraje Webid kraje pod který stanice spadá
     */
    public Stanice(String inNazev, Integer inWebidStanice, Integer inWebidKraje) {
        this.nazevStanice = inNazev;
        this.webidStanice = inWebidStanice;
        this.webidKraje = inWebidKraje;
        this.hodnoty = new LinkedList<>();
    }

    /**
     * Vloží novou hodnotu srážek na konec zřetězeného seznamu.
     * <p>
     * @param input Číselná hodnota srážkového údaje
     */
    public void pushValue(float input) {
        this.hodnoty.add(input);
    }

    /**
     * Vrátí název stanice.
     * <p>
     * @return Název danné stanice
     */
    public String getNazevStanice() {
        return nazevStanice;
    }

    public Integer getWebidStanice() {
        return webidStanice;
    }
    

    /**
     * Vrátí zřetězený seznam uložených srážkových hodnot, přičem kalendářně
     * nejnovější záznam je vždy na konci seznamu.
     * <p>
     * @return Zřetězený seznam srážkových hodnot
     */
    public LinkedList<Float> getHodnoty() {
        return hodnoty;
    }
}
