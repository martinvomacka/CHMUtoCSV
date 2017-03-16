package chmutocsv;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class CHMUtoCSV extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoe = new FXMLLoader(getClass().getResource("FXML/HlavniOkno.fxml"));
        Parent root = fxmlLoe.load();
        HlavniOknoController myController = (HlavniOknoController)fxmlLoe.getController();
        myController.setParentApp(this);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("ČHMÚ HPPS to CSV - ©Martin Vomáčka 2017");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("cloud.png")));
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
               Platform.exit();
               System.exit(0);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
