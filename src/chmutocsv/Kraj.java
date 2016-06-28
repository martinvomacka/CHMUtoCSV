package chmutocsv;

import java.util.HashMap;

/**
 * Třída obstarávající funkcionalitu na úrovni jednoho kraje. Obsahuje hešovaný
 * seznam stanic, ke kterým je možný přístup pomocí jejich názvů a zastřešuje
 * operace nad tímto seznamem. Každý kraj musí být identifikován svým číslením
 * ID.
 * <p>
 * @author Martin Vomáčka
 */
public class Kraj {

    /**
     * Uchovává číselný údaj ID kraje, použitého v URL.
     */
    private final Integer webidKraje;
    /**
     * Uchovává hešovaný seznam všech stanic, které tento kraj obsahuje.
     * Adresace je provedena pomocí názvu stanice a každý prvek pak nese číslené
     * ID této stanice, které se používá v URL.
     */
    private final HashMap<String, Integer> staniceKraje;

    /**
     * Konstruktor třídy, povinná je specifikace čísleného ID kraje. Provede
     * inicializace hešovaného seznamu (prázdný seznam).
     * <p>
     * @param initWebid Číselné ID kraje
     */
    public Kraj(Integer initWebid) {
        this.webidKraje = initWebid;
        this.staniceKraje = new HashMap<>();
    }

    /**
     * Vloží stanici o zadaném jménu a se zadanou číselnou hodnotou ID stanice
     * do hešovaného seznamu. Také je prováděna kontrola, zda se již stanice v
     * seznamu nenachází. Pokud se stanice o zadaném názvu v seznamu nachází,
     * vložení neproběhne a vrací false, v opačném případě stanici vloží do
     * hešovaného seznamu a vrátí true.
     * <p>
     * @param jmenoStanice Název vkládané stanice - klíč do hešovaného seznamu
     * @param webidStanice Číselná hodnota ID stanice - vkládaná hodnota do
     *                     hešovaného seznamu
     * @return Boolean hodnota udávající úspěšnost vložení stanice do kraje.
     */
    public boolean vlozitStanici(String jmenoStanice, Integer webidStanice) {
        if (staniceKraje.containsKey(jmenoStanice)) {
            return false;
        } else {
            staniceKraje.put(jmenoStanice, webidStanice);
            return true;
        }
    }

    /**
     * Pomocná metoda pro výpis všech stanic danného kraje ve formátu: "Stanice:
     * [název] ID: [číselná hodnota]".
     */
    public void printStanice() {
        Object seznamNazvuStanic[] = staniceKraje.keySet().toArray();
        for (Object iterator : seznamNazvuStanic) {
            System.out.println("Stanice: " + (String) iterator + " ID: " + staniceKraje.get((String) iterator));
        }
    }

    /**
     * Vrátí hešovaný seznam stanic danného kraje.
     * <p>
     * @return Hešovaný seznam stanic z danného kraje
     */
    public HashMap getSeznam() {
        return staniceKraje;
    }

    /**
     * Vrátí číselné ID stanice dle poskytnutého názvu stanice. Pokud stanice
     * tohoto jména v seznamu neexistuje, číselné ID je {@code null}.
     * <p>
     * @param jmenoStanice Název stanice požadované stanice
     * @return Vrací číselné ID stanice, případně {@code null}, pokud stanice v
     *         kraji neexsituje.
     */
    public int getStaniceWebid(String jmenoStanice) {
        return staniceKraje.get(jmenoStanice);
    }

    /**
     * Vrátí počet stanic danného kraje.
     * <p>
     * @return Počet stanic v danném kraji
     */
    public int getPocetStanic() {
        return staniceKraje.size();
    }

    /**
     * Vrátí číselné ID danného kraje.
     * <p>
     * @return Číselné ID danného kraje
     */
    public Integer getWebidKraje() {
        return webidKraje;
    }
}
