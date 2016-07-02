/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chmutocsv;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 *
 * @author Vomec
 */
public class HlavniOknoController implements Initializable {
    private final String filename = "config.xml";
    private HashMap<String, Kraj> seznamKraju;
    private Object nacteneKraje[];
    private int nactenyPocet;
    public ArrayList<String> seznamVybranychStanic;
    public static LinkedList<Stanice> nacteneStanice;
    private final XMLParser parserXML = new XMLParser();
    private final Updater kontrola = new Updater();
    public ObservableList<String> ob;
    private Application parentApp;
    
    @FXML
    public DatePicker dOd;
    @FXML
    public DatePicker dDo;
    @FXML
    public Label statusBar;
    @FXML
    public Circle cKolo;
    @FXML
    public Button bPridat;
    @FXML
    public Button bExport;
    @FXML
    public Button bOdebrat;
    @FXML
    public Button bOdebratVse;
    @FXML
    public Menu menu1;
    @FXML
    public Menu menu2;
    @FXML
    public TreeView<String> tSeznam;
    @FXML
    public ListView<String> lVybrane;
    
    @FXML
    private void updateButtonAction() {
        disableControls(true);
        Task <Boolean> task = new Task<Boolean>() {
            @Override public Boolean call() throws InterruptedException {
                updateMessage("Probíhá aktualizace seznamu stanic. Prosím vyčkejte!");
                statusBar.setTextFill(Color.GREEN);
                if(!kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php?day_offset=-1"))
                    return false;
                seznamKraju=kontrola.updateKraje();
                nacteneKraje = seznamKraju.keySet().toArray();
                for (Object iterator : nacteneKraje) {
                    if(!kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php?day_offset=-1&fkraj="+seznamKraju.get((String)iterator).getWebidKraje()))
                        return false;
                    kontrola.naplnKraj(seznamKraju.get((String)iterator));
                }
                parserXML.exportToXML(seznamKraju, filename);
                return true;
            }
        };
        statusBar.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> {
            statusBar.textProperty().unbind();
            disableControls(false);
            if(!task.getValue()) {
                statusBar.setText("Chyba přístupu k URL!");
                statusBar.setTextFill(Color.RED);
            }
            else {
                statusBar.setText("Konfigurační soubor úspěšně vytvořen.");
                statusBar.setTextFill(Color.GREEN);
                loadTreeItems();
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML
    private void checkUpdateButtonAction() {
        disableControls(true);
        Task <Boolean> task = new Task<Boolean>() {
            @Override public Boolean call() throws InterruptedException {
                updateMessage("Kontrola seznamu stanic. Prosím vyčkejte!");
                statusBar.setTextFill(Color.GREEN);
                seznamKraju = parserXML.importFromXML(filename);
                updatePocetStanic();
                if(kontrola.setURL("http://hydro.chmi.cz/hpps/hpps_act_rain.php?day_offset=-1")) {
                    int webpocet = kontrola.isUpdateNeeded();
                    if(webpocet==-1)
                        return false;
                    else if(webpocet != nactenyPocet) {
                        updateMessage("Je potřeba aktualizace seznamu stanic! Konfigurační soubor: "+nactenyPocet+" \u2260 Web ČHMÚ: "+webpocet);
                        statusBar.setTextFill(Color.ORANGERED);
                    }
                    else {
                        updateMessage("Seznam stanic je aktuální. Není potřeba aktualizace.");
                        statusBar.setTextFill(Color.GREEN);
                    }
                    return true;
                }
                else
                    return false;
            }
        };
        statusBar.textProperty().bind(task.messageProperty());
        task.setOnSucceeded(e -> {
            statusBar.textProperty().unbind();
            disableControls(false);
            if(!task.getValue()) {
                statusBar.textProperty().unbind();
                statusBar.setText("Chyba přístupu k URL!");
                statusBar.setTextFill(Color.RED);
                disableControls(false);
            }
        });
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
    
    @FXML
    private void bCSVExportPressed() {
        if(seznamVybranychStanic.isEmpty()) {
            statusBar.setText("Nevybrána žádná stanice!");
            statusBar.setTextFill(Color.ORANGERED);
            return;
        }
        else
            statusBar.setTextFill(Color.GREEN);
        nacteneStanice = new LinkedList<>();
        generateStanice();
        Task dataLoaderThread = new DataLoaderController(this, nacteneStanice);
        statusBar.textProperty().bind(dataLoaderThread.messageProperty());
        cKolo.setFill(Color.ORANGERED);
        disableControls(true);
        dataLoaderThread.setOnSucceeded(e -> {
            statusBar.textProperty().unbind();
            cKolo.setFill(Color.GREEN);
            disableControls(false);
            // this message will be seen.
            statusBar.setText("Načítání dat proběhlo úspěšně");
            statusBar.setTextFill(Color.GREEN);
            LocalDate dateOd = dOd.getValue();
            LocalDate dateDo = dDo.getValue();
            LocalDate first = LocalDate.now().minusDays(6);
            int dnuCelkem = Period.between(dateOd, dateDo).getDays();
            int dnuOdPrvniho = Period.between(first, dateOd).getDays();
            CSVParser parserCSV = new CSVParser(nacteneStanice, dnuCelkem, dnuOdPrvniho, "test.csv");
            parserCSV.csvExport();
        });
        Thread thread = new Thread(dataLoaderThread);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        File configFile = new File(filename);
        if(configFile.exists() && configFile.isFile()) {
            seznamKraju = parserXML.importFromXML(filename);
        }
        else {
            statusBar.setTextFill(Color.RED);
            statusBar.setText("Konfigurační soubor neexistuje - proveďte jeho aktualizaci!");
            disableControls(true);
            return;
        }
        final Callback<DatePicker, DateCell> dayCellFactory = 
            new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                           
                            if (item.isBefore(dOd.getValue())
                                ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                            }
                            if (item.isAfter(LocalDate.now())
                                ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                            } 
                    }
                };
            }
        };
        final Callback<DatePicker, DateCell> day2CellFactory = 
            new Callback<DatePicker, DateCell>() {
                @Override
                public DateCell call(final DatePicker datePicker) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty);
                           
                            if (item.isBefore(LocalDate.now().minusDays(6))
                                ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                            }
                            if (item.isAfter(LocalDate.now())
                                ) {
                                    setDisable(true);
                                    setStyle("-fx-background-color: #ffc0cb;");
                            } 
                    }
                };
            }
        };
        dOd.setDayCellFactory(day2CellFactory);
        dOd.setValue(LocalDate.now().minusDays(6));
        dDo.setDayCellFactory(dayCellFactory);
        dDo.setValue(LocalDate.now());
        updatePocetStanic();
        statusBar.setText(statusBar.getText()+" Aktuální počet všech záznamů: "+nactenyPocet);
        loadTreeItems();
        seznamVybranychStanic = new ArrayList<>();
    }
    
    private void disableControls(boolean input) {
        bOdebrat.setDisable(input);
        bOdebratVse.setDisable(input);
        bPridat.setDisable(input);
        tSeznam.setDisable(input);
        lVybrane.setDisable(input);
        bExport.setDisable(input);
        dDo.setDisable(input);
        dOd.setDisable(input);
    }
    
    private void generateStanice() {
        int staniceID;
        for (String seznamVybranychStanic1 : seznamVybranychStanic) {
            for (Object nacteneKraje1 : nacteneKraje) {
                staniceID = seznamKraju.get((String) nacteneKraje1).getStaniceWebid(seznamVybranychStanic1);
                if (staniceID>=0) {
                    nacteneStanice.add(new Stanice(seznamVybranychStanic1, staniceID));
                    break;
                }
            }
        }
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
        if(tSeznam.getSelectionModel().getSelectedItem().getValue().equals("Kraje"))
            return;
        String vybranyParent = tSeznam.getSelectionModel().getSelectedItem().getParent().getValue();
        if(vybranyParent.equals("Kraje")) {
            addAllFromKraj(tSeznam.getSelectionModel().getSelectedItem().getValue());
        }
        else {
            if(!seznamVybranychStanic.contains(tSeznam.getSelectionModel().getSelectedItem().getValue())){
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
    
    public void setParentApp(Application input) {
        this.parentApp = input;
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
        Stage stage = (Stage) bOdebratVse.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void aboutApp() {
        javafx.application.Platform.runLater(() -> {
            Stage stage = new Stage();
            FXMLLoader popUpLoader = new FXMLLoader();
            try {
                popUpLoader.load(getClass().getResource("/chmutocsv/FXML/OAplikaci.fxml").openStream());
            } catch (IOException ex) {
                Logger.getLogger(HlavniOknoController.class.getName()).log(Level.SEVERE, null, ex);
            }
            Parent root = popUpLoader.getRoot();
            OAplikaciController newCont = popUpLoader.getController();
            newCont.setParentApp(parentApp);
            stage.setScene(new Scene(root));
            stage.setTitle("O aplikaci");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.showAndWait();
        });
    }
}
