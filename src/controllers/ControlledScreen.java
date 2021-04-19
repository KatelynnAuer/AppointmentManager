package controllers;

import models.Session;
import models.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Screen utils parent class
 * @author Katelynn Auer
 */
public class ControlledScreen implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public <E> Class<Void> loadScreen(ActionEvent event, E controller, String resourceName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourceName));
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        loader.setController(controller);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        return Void.class;
    }
    public void loadMain(ActionEvent event, Session session) throws IOException {
        loadScreen(event, new MainScreen(session), "/views/MainScreen.fxml");
    }

    public void alert(Alert.AlertType type, String title, String header){
        Util.alert(type,title,header);
    }
    public void alert(Alert.AlertType type, String title, String header, String content){
        Util.alert(type,title,header,content);
    }
    public void exitProgram(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
