package models;

import controllers.LoginScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main Entry
 * @author Katelynn Auer
 */

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Session session = new Session();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/LoginScreen.fxml"));
        LoginScreen controller = new LoginScreen(session);
        loader.setController(controller);
        primaryStage.setTitle("DB Scheduling System");
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
