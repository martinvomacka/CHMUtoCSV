/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chmutocsv;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 *
 * @author Vomec
 */
public class FXMLDocumentController implements Initializable {
    private final String filename = "config.xml";
    private HashMap<String, Kraj> seznamKraju;
    private Object nacteneKraje[];
    private int nactenyPocet;
    private final XMLParser parser = new XMLParser(this);
    
    @FXML
    public Label statusBar;
    @FXML
    public Button checkUpdate;
    
    @FXML
    private void updateButtonAction() {
        Updater kontrola = new Updater();
        kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php");
        seznamKraju=kontrola.updateKraje();
        nacteneKraje = seznamKraju.keySet().toArray();
        for (Object iterator : nacteneKraje) {
            kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php?fkraj="+seznamKraju.get((String)iterator).getWebidKraje());
            kontrola.naplnKraj(seznamKraju.get((String)iterator));
        }
        parser.exportToXML(seznamKraju, filename);
        checkUpdate.setDisable(false);
    }
    
    @FXML
    private void checkUpdateButtonAction() {
        seznamKraju = parser.importFromXML(filename);
        updatePocetStanic();
        Updater kontrola = new Updater();
        kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php");
        int webpocet = kontrola.isUpdateNeeded();
        if(webpocet != nactenyPocet) {
            statusBar.setText("Je potřeba aktualizace seznamu stanic! Konfigurační soubor: "+nactenyPocet+" =/= Web ČHMÚ: "+webpocet);
            statusBar.setStyle("-fx-font-weight: bold;"+statusBar.getStyle());
            statusBar.setTextFill(Color.ORANGERED);
        }
        else {
            statusBar.setText("Seznam stanic je aktuální.");
            statusBar.setTextFill(Color.GREEN);
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        File configFile = new File(filename);
        if(configFile.exists() && configFile.isFile()) {
            seznamKraju = parser.importFromXML(filename);
        }
        else {
            statusBar.setText("Konfigurační soubor neexistuje - proveďte jeho aktualizaci!");
            statusBar.setTextFill(Color.RED);
            checkUpdate.setDisable(true);
            return;
        }
        updatePocetStanic();
        statusBar.setText(statusBar.getText()+" Aktuální počet všech záznamů: "+nactenyPocet);
    }
    
    private void updatePocetStanic() {
        nacteneKraje = seznamKraju.keySet().toArray();
        nactenyPocet = 0;
        for (Object iterator : nacteneKraje) {
            nactenyPocet += seznamKraju.get((String) iterator).getPocetStanic();
        }
    }
}
