package controllers;

import models.Session;
import models.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static models.Appointment.cApp;
import static models.Util.*;

/**
 * New Appointment Screen controller
 * @author Katelynn Auer
 */
public class NewAppointmentScreen extends TranslatableScreen{
  private final Session session;

  private final List<String> contacts = new ArrayList<>();
  private final List<String> customers = new ArrayList<>();
  private final List<String> users = new ArrayList<>();

  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  @FXML private ComboBox<String> contactComboBox;
  @FXML private ComboBox<String> customerComboBox;
  @FXML private ComboBox<String> endHoursComboBox;
  @FXML private ComboBox<String> endMinsComboBox;
  @FXML private ComboBox<String> startHoursComboBox;
  @FXML private ComboBox<String> startMinsComboBox;
  @FXML private ComboBox<String> userSelectComboBox;

  @FXML private DatePicker endDatePicker;
  @FXML private DatePicker startDatePicker;

  @FXML private Label heading;
  @FXML private Label aidLabel;
  @FXML private Label contactLabel;
  @FXML private Label cuiLabel;
  @FXML private Label descriptionLabel;
  @FXML private Label endLabel;
  @FXML private Label headTitle;
  @FXML private Label locationLabel;
  @FXML private Label startLabel;
  @FXML private Label titleLabel;
  @FXML private Label typeLabel;

  @FXML private TextField aidTextField;
  @FXML private TextField descTextField;
  @FXML private TextField locationTextField;
  @FXML private TextField titleTextField;
  @FXML private TextField typeTextField;

  /**
   * New Appointments Screen constructor
   * @param session current session
   */
  public NewAppointmentScreen(Session session) { this.session = session; }

  /**
   * Initialize the screen
   * @param url fxml url
   * @param resourceBundle Resource Bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    initComboBoxes();
    translateLabels();
    times();
  }

  /**
   * Translate the labels to locale
   */
  private void translateLabels() {
    translate(heading, "LOGIN_BANNER");
    translate(titleLabel, "NEW_APPOINTMENT");
    translate(aidLabel, "ID");
    translate(aidTextField, "DISABLED");
    translate(titleLabel, "NEW_CUSTOMER");
    translate(titleLabel, "TITLE");
    translate(titleTextField, "TITLE");
    translate(descriptionLabel, "DESCRIPTION");
    translate(descTextField, "DESCRIPTION");
    translate(locationLabel, "LOCATION");
    translate(locationTextField, "LOCATION");
    translate(contactLabel, "CONTACT");
    translate(typeLabel, "TYPE");
    translate(typeTextField, "TYPE");
    translate(startLabel, "START_DATE");
    translate(endLabel, "END_DATE");
    translate(cuiLabel, "CUSTOMER_ID");
    translate(saveButton, "SAVE");
    translate(cancelButton, "CANCEL");
    translate(headTitle, "NEW_APPOINTMENT");
  }

  /**
   * Initialize Combo Boxes
   * Lambda: Fill Combo Box with customer names
   */
  private void initComboBoxes() {
    clear();

    for (int i = 0; i < session.getCache().getContacts().size(); i++) {
      String contact = session.getCache().getContacts().get(i).getName();
      contactComboBox.getItems().add(contact);
      this.contacts.add(i, contact);
    }

    // Lambda
    session.getCache().getCustomers().forEach(customer -> {
      String name = customer.getName();
      this.customerComboBox.getItems().add(name);
      this.customers.add(name);
    });

    try {
      ResultSet result = sql( "SELECT * FROM users");
      while (result.next()) {
        String user = result.getString("User_Name");
        this.userSelectComboBox.getItems().add(user);
        this.users.add(user);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Clear Combo Boxes and lists
   */
  private void clear(){
    this.contactComboBox.getItems().clear();
    this.customerComboBox.getItems().clear();
    this.userSelectComboBox.getItems().clear();
    this.contacts.clear();
    this.customers.clear();
    this.users.clear();
  }
  /**
   * Format times
   */
  private void times() {
    ZoneId estZone = ZoneId.of("America/New_York");

    ObservableList<String> startHrs = FXCollections.observableArrayList();
    ObservableList<String> endHrs = FXCollections.observableArrayList();
    ObservableList<String> startMinutes = FXCollections.observableArrayList();
    ObservableList<String> endMinutes = FXCollections.observableArrayList();

    LocalDateTime startTime = LocalDateTime.parse("2020-10-20 08:00", date24(false));
    LocalDateTime endTime = LocalDateTime.parse("2020-10-20 22:00", date24(false));

    ZonedDateTime EstStart = ZonedDateTime.of(startTime, estZone);
    ZonedDateTime EstEnd = ZonedDateTime.of(endTime, estZone);
    ZonedDateTime localStart = EstStart.withZoneSameInstant(ZoneId.systemDefault());
    ZonedDateTime localEnd = EstEnd.withZoneSameInstant(ZoneId.systemDefault());

    for (int i= localStart.getHour(); i <= localEnd.getHour(); i++) {
      String e = i < 10 ? "0" + i : Integer.toString(i);
      if (i < localEnd.getHour() && localEnd.getMinute() == 0) {
        startHrs.add(Integer.toString(i));

      }else startHrs.add(e);
      endHrs.add(e);
    }
    startMinutes.addAll("00", "15", "30", "45");
    endMinutes.addAll("00", "15", "30", "45");

    startHoursComboBox.setItems(startHrs);
    startMinsComboBox.setItems(startMinutes);
    endHoursComboBox.setItems(endHrs);
    endMinsComboBox.setItems(endMinutes);
  }


  /**
   * Update
   * @param event Action Event
   */
  @FXML void update(ActionEvent event) {
    ObservableList<String> endMinutes = FXCollections.observableArrayList();
    ObservableList<String> startMinutes = FXCollections.observableArrayList();

    endMinsComboBox.getItems().clear();
    String endHrValue = endHoursComboBox.getValue();

    ZoneId estZone = ZoneId.of("America/New_York");

    LocalDateTime startTime = LocalDateTime.parse("2020-10-20 08:00", date24(false));
    LocalDateTime endTime = LocalDateTime.parse("2020-10-20 22:00", date24(false));
    ZonedDateTime EstStart = ZonedDateTime.of(startTime, estZone);
    ZonedDateTime EstEnd = ZonedDateTime.of(endTime, estZone);
    ZonedDateTime localStart = EstStart.withZoneSameInstant(ZoneId.systemDefault());
    ZonedDateTime localEnd = EstEnd.withZoneSameInstant(ZoneId.systemDefault());

    if (Integer.parseInt(endHrValue) == localEnd.getHour() && localEnd.getMinute() == 0) {
      endMinutes.addAll("00");
    } else if (Integer.parseInt(endHrValue) == localStart.getHour()  && localStart.getMinute() == 0) {
      endMinutes.addAll("15", "30", "45");
    }
    else {
      endMinutes.addAll("00", "15", "30", "45");
    }

    startMinutes.addAll("00", "15", "30", "45");

    endMinsComboBox.setItems(endMinutes);
    startMinsComboBox.setItems(startMinutes);
  }

  /**
   * Cancel appointment add
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML void cancel(ActionEvent event) throws IOException {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(language().getString("CANCEL_CONFIRM"));
    alert.setContentText(language().getString("CANCEL_APPOINTMENT_TEXT"));
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK)
      loadScreen(event, new AppointmentsScreen(session), "/views/AppointmentScreen.fxml");
  }

  /**
   * Save appointment
   * @param event Action event
   * @throws SQLException Exception on sql failure
   * @throws IOException Exception on failure
   */
  @FXML void save(ActionEvent event) throws SQLException, IOException {
    int newId = newId("appointments", "Appointment_ID");

    LocalDate st = startDatePicker.getValue();
    LocalDate et = endDatePicker.getValue();

    String tft = titleTextField.getText();
    String lft = locationTextField.getText();
    String dft = descTextField.getText();
    String shv = startHoursComboBox.getValue();
    String smv = startMinsComboBox.getValue();
    String ehv = endHoursComboBox.getValue();
    String emv = endMinsComboBox.getValue();
    String cft = contactComboBox.getSelectionModel().getSelectedItem();
    String cift = customerComboBox.getSelectionModel().getSelectedItem();
    String tyft = typeTextField.getText();

    if (tft.equals("") || dft.equals("") || lft.equals("") || cft == null
            || cift == null || tyft.equals("") || st == null || shv == null || smv== null || et == null ||
            ehv== null || emv== null)
      alert(Alert.AlertType.ERROR,
              language().getString("ERROR"),
              language().getString("INVALID_EMPTY"));
    else {

      Timestamp utcStart = Util.tutct(st, shv, smv);
      Timestamp utcend = Util.tutct(et, ehv, emv);
      Timestamp lstart = Util.ttlt(st, shv, smv);
      Timestamp lend = Util.ttlt(et, ehv, emv);
      System.out.println((utcStart));
      System.out.println(utcend);
      Timestamp current_date = new Timestamp( new java.sql.Date(System.currentTimeMillis()).getTime());

      String creator = session.getUser();
      String lub = session.getUser();
      String cuift = customerComboBox.getSelectionModel().getSelectedItem();
      String uft = userSelectComboBox.getSelectionModel().getSelectedItem();

      int contactId = 0;
      int customerId = 0;
      int userId = 0;

      ResultSet contactResult = sql("SELECT * FROM contacts WHERE Contact_Name = '"+cft+"';") ;
      ResultSet customerResult = sql("SELECT * FROM customers WHERE Customer_Name = '"+cuift+"';") ;
      ResultSet userResult = sql("SELECT * FROM users WHERE User_Name = '"+uft+"';") ;

      while (contactResult.next())
        contactId = contactResult.getInt("Contact_ID");
      while (customerResult.next())
        customerId = customerResult.getInt("Customer_ID");
      while (userResult.next())
        userId = userResult.getInt("User_ID");
      if (Util.checkAppointment(utcStart, utcend, customerId, newId, language())) {
        session.getCache().newAppointment(cApp(newId, tft, dft, lft, tyft, utcend, utcStart, current_date, contactId, lstart, lend, creator, lub, customerId, userId));
        loadScreen(event, new AppointmentsScreen(session), "/views/AppointmentScreen.fxml");
      }
    }
  }
}
