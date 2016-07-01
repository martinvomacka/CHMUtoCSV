package chmutocsv;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

public class PopUpWindowController implements Initializable {
    /**
     * Tlačítko pro zavření okna.
     */
    @FXML
    public Button bZavrit;
    @FXML
    public Hyperlink hEmail;
    
    private Application parentApp;
    
    /**
     * Metoda obsluhujici tlacitko zavření okna.
     */
    @FXML
    public void bZavritAction() {
        Stage stage=(Stage)bZavrit.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void hEmailPressed(ActionEvent event) {
        hEmail.setVisited(false);
        HostServices services = parentApp.getHostServices();
        services.showDocument("mailto:mvomacka@seznam.cz?subject=CHMUtoCSV");
    }
    
    public void setParentApp(Application input) {
        this.parentApp = input;
    }
    
    /**
     * Inicializacni metoda JavaFX, vola se pri tvorbe tridy a inicializuje GUI.
     * @param url pomocny odkaz na dodatecne externi zdroje
     * @param rb pomocny odkaz na dodatecne zdroje tridy
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hEmail.setStyle("-fx-focus-color: transparent;");
    }
}