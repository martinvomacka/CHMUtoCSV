/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chmutocsv;

/**
 *
 * @author Vomec
 */
public class Stanice {
    private final String nazev;
    private final String webid;

    public Stanice(String inNazev, String inWebid) {
        this.nazev = inNazev;
        this.webid = inWebid;
    }

    public String getNazev() {
        return nazev;
    }

    public String getWebid() {
        return webid;
    }
}
