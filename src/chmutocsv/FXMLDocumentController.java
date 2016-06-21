/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chmutocsv;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Vomec
 */
public class FXMLDocumentController implements Initializable {
    private final String filename = "config.xml";
    private HashMap<String, Kraj> seznamKraju;
    private Object nacteneKraje[];
    private int nactenyPocet;
    public ArrayList<String> seznamVybranychStanic;
    public ArrayList<StaniceData> nacteneStanice;
    private final XMLParser parser = new XMLParser(this);
    private final Updater kontrola = new Updater();
    public ObservableList<String> ob;
    
    @FXML
    public Label statusBar;
    @FXML
    public Button checkUpdate;
    @FXML
    public TreeView<String> tSeznam;
    @FXML
    public ListView<String> lVybrane;
    
    @FXML
    private void testRead() {
        kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_srzstationdyn.php?seq="+"20293175");
        StaniceData test = new StaniceData("Luční bouda", 20293175);
        kontrola.readValues(test);
    }
    
    @FXML
    private void updateButtonAction() {
        kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php");
        seznamKraju=kontrola.updateKraje();
        nacteneKraje = seznamKraju.keySet().toArray();
        for (Object iterator : nacteneKraje) {
            kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php?fkraj="+seznamKraju.get((String)iterator).getWebidKraje());
            kontrola.naplnKraj(seznamKraju.get((String)iterator));
        }
        parser.exportToXML(seznamKraju, filename);
        checkUpdate.setDisable(false);
        tSeznam.setDisable(false);
        loadTreeItems();
    }
    
    @FXML
    private void checkUpdateButtonAction() {
        seznamKraju = parser.importFromXML(filename);
        updatePocetStanic();
        kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php");
        int webpocet = kontrola.isUpdateNeeded();
        if(webpocet != nactenyPocet) {
            statusBar.setText("Je potřeba aktualizace seznamu stanic! Konfigurační soubor: "+nactenyPocet+" =/= Web ČHMÚ: "+webpocet);
            statusBar.setStyle("-fx-font-weight: bold;"+statusBar.getStyle());
            statusBar.setTextFill(Color.ORANGERED);
        }
        else {
            statusBar.setText("Seznam stanic je aktuální. Není potřeba aktualizace.");
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
            tSeznam.setDisable(true);
            return;
        }
        updatePocetStanic();
        statusBar.setText(statusBar.getText()+" Aktuální počet všech záznamů: "+nactenyPocet);
        loadTreeItems();
        seznamVybranychStanic = new ArrayList<>();
    }

    private void loadTreeItems() {
        TreeItem<String> root = new TreeItem<>("Kraje");
        root.setExpanded(true);
        tSeznam.setRoot(root);
        int size = seznamKraju.size();
        int sizeKraj;
        for (int i = 0; i < size; i++) {
            TreeItem<String> temp = new TreeItem<>((String)nacteneKraje[i]);
            sizeKraj=seznamKraju.get((String)nacteneKraje[i]).getPocetStanic();
            Object seznamStanic[] = seznamKraju.get((String)nacteneKraje[i]).getSeznam().keySet().toArray();
            for(int j=0; j<sizeKraj;j++)
                temp.getChildren().add(new TreeItem<>((String)seznamStanic[j]));
            temp.setExpanded(false);
            root.getChildren().add(temp);
        }
    }
    
    private void loadStaniceData(String nazevStanice, String nazevKraje) {
        StaniceData temp = new StaniceData(nazevStanice, seznamKraju.get(nazevKraje).getStaniceWebid(nazevStanice));
    }
    
    private void addAllFromKraj(String nazevKraje) {
        Object seznamStanic[] = seznamKraju.get(nazevKraje).getSeznam().keySet().toArray();
        int size = seznamKraju.get(nazevKraje).getPocetStanic();
        String jmeno;
        for (int i = 0; i < size; i++) {
            jmeno=(String)seznamStanic[i];
            if(!seznamVybranychStanic.contains(jmeno))
                seznamVybranychStanic.add(jmeno);
        }
    }
    
    public void bPridatPressed(ActionEvent event) {
        String vybranyParent = tSeznam.getSelectionModel().getSelectedItem().getParent().getValue();
        if(vybranyParent.equals("Kraje")) {
            addAllFromKraj(tSeznam.getSelectionModel().getSelectedItem().getValue());
            System.out.println("Přidat vše z kraje "+tSeznam.getSelectionModel().getSelectedItem().getValue());
        }
        else {
            if(!seznamVybranychStanic.contains(tSeznam.getSelectionModel().getSelectedItem().getValue())) {
                seznamVybranychStanic.add(tSeznam.getSelectionModel().getSelectedItem().getValue());
            }
        }
        ob = FXCollections.observableList(seznamVybranychStanic);
        lVybrane.setItems(ob);
    }
    
    public void bOdebratVsePressed(ActionEvent event) {
        seznamVybranychStanic.clear();
        ob = FXCollections.observableList(seznamVybranychStanic);
        lVybrane.setItems(ob);
    }
    
    public void bOdebratPressed(ActionEvent event) {
        if(lVybrane.getSelectionModel().selectedItemProperty().getValue()!=null) {
            seznamVybranychStanic.remove(lVybrane.getSelectionModel().selectedItemProperty().getValue());
            ob = FXCollections.observableList(seznamVybranychStanic);
            lVybrane.setItems(ob);
        }
    }
    
    private void updatePocetStanic() {
        nacteneKraje = seznamKraju.keySet().toArray();
        nactenyPocet = 0;
        for (Object iterator : nacteneKraje) {
            nactenyPocet += seznamKraju.get((String) iterator).getPocetStanic();
        }
    }
    
    @FXML
    public void closeApp() {
        Stage stage = (Stage) checkUpdate.getScene().getWindow();
        stage.close();
    }
}
