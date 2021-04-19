package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import models.Session;

/**
 * Main Screen Controller
 * @author Katelynn Auer
 */

public class MainScreen extends TranslatableScreen{
    private final Session session;

    @FXML private Label heading;
    @FXML private Label locationLbl;

    @FXML private Button appointmentButton;
    @FXML private Button customerButton;
    @FXML private Button exitButton;
    @FXML private Button reportsButton;
    @FXML private ToggleGroup radioToggleGroup;

    /**
     * Main Screen Constructor
     * @param session current session
     */
    public MainScreen(Session session) {
        this.session = session;
    }

    /**
     * Initialize the screen
     * @param url fxml url
     * @param resourceBundle Resource Bundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        translateLabels();
        locationLbl.setText(this.session.getLocale().getDisplayCountry());
    }

    /**
     * Translate the labels to locale
     */
    private void translateLabels() {
        translate(heading, "LOGIN_BANNER");
        translate(appointmentButton, "APPOINTMENTS");
        translate(reportsButton, "REPORTS");
        translate(customerButton, "CUSTOMERS");
        translate(exitButton, "EXIT");
    }

    /**
     * Load Appointment Screen
     * @param event Action Event
     * @throws IOException Exception on failure
     */
    @FXML
    void AppointmentScreen(ActionEvent event) throws IOException {
        loadScreen(event, new AppointmentsScreen(session), "/views/AppointmentScreen.fxml");
    }

    /**
     * Load Customer Screen
     * @param event Action Event
     * @throws IOException Exception on failure
     */
    @FXML
    void CustomersScreen(ActionEvent event) throws IOException {
        loadScreen(event,new CustomersScreen(session), "/views/CustomerScreen.fxml");
    }

    /**
     * Load Reports Screen
     * @param event Action Event
     * @throws IOException Exception on failure
     */
    @FXML
    void ReportsScreen(ActionEvent event) throws IOException {
        loadScreen(event,new ReportsScreen(session), "/views/ReportsScreen.fxml");
    }

    /**
     * Exit the application
     * @param event Action Event
     */
    @FXML
    void exit(ActionEvent event) {
        exitProgram(event);
    }
}
