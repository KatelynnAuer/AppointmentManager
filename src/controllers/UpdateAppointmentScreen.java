package controllers;

import models.SQL;
import models.Session;
import models.Util;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.sql.PreparedStatement;

import static models.Util.sql;

/**
 * Update Appointment Screen Controller
 * @author Katelynn Auer
 */
public class UpdateAppointmentScreen extends TranslatableScreen{

  private final Session session;
  private final Appointment appointment;
  private String customer;
  private String contact;
  private String user;

  @FXML private ComboBox<String> contactComboBox;
  @FXML private ComboBox<String> customerComboBox;
  @FXML private ComboBox<String> endHourComboBox;
  @FXML private ComboBox<String> endMinsComboBox;
  @FXML private ComboBox<String> startHourComboBox;
  @FXML private ComboBox<String> startMinsComboBox;
  @FXML private ComboBox<String> userComboBox;

  @FXML private DatePicker endDatePicker;
  @FXML private DatePicker startDatePicker;

  @FXML private Label heading;
  @FXML private Label banner;
  @FXML private Label aidLabel;
  @FXML private Label contactLabel;
  @FXML private Label customerLabel;
  @FXML private Label descLabel;
  @FXML private Label endLabel;
  @FXML private Label locationLabel;
  @FXML private Label startLabel;
  @FXML private Label titleLabel;
  @FXML private Label typeLabel;
  @FXML private Label userLabel;


  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  @FXML private TextField aidTextField;
  @FXML private TextField descTextField;
  @FXML private TextField locationTextField;
  @FXML private TextField titleTextField;
  @FXML private TextField typeTextField;


  /**
   * Appointments Screen Constructor
   * @param session current session
   * @param appointment selected appointment to update
   */
  public UpdateAppointmentScreen(Session session, Appointment appointment) {
    this.session = session;
    this.appointment = appointment;
  }

  /**
   * Initialize the screen
   * @param url fxml url
   * @param resourceBundle Resource Bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    translateLabels();
    initTextFields();
    initDatePickers();
    fillComboBoxes();
    initComboBoxes();
    times();
  }

  /**
   * Translate the labels to locale
   */
  private void translateLabels() {
    translate(heading, "LOGIN_BANNER");
    translate(banner, "UPDATE");
    translate(titleLabel, "APPOINTMENT_UPDATE");
    translate(aidLabel, "ID");
    translate(aidTextField, "DISABLED");
    translate(titleLabel, "TITLE");
    translate(titleTextField, "TITLE");
    translate(descLabel, "DESCRIPTION");
    translate(descTextField, "DESCRIPTION");
    translate(locationLabel, "LOCATION");
    translate(locationTextField, "LOCATION");
    translate(contactLabel, "CONTACT");
    translate(typeLabel, "TYPE");
    translate(typeTextField, "TYPE");
    translate(startLabel, "START_DATE");
    translate(endLabel, "END_DATE");
    translate(customerLabel, "CUSTOMER_ID");
    translate(saveButton, "SAVE");
    translate(cancelButton, "CANCEL");
  }

  private void initTextFields() {
    aidTextField.setText(Integer.toString(appointment.getAid()));
    titleTextField.setText(appointment.getTitle());
    descTextField.setText(appointment.getDescription());
    locationTextField.setText(appointment.getLocation());
    typeTextField.setText(appointment.getType());
  }

  /**
   * Sets Combo Boxes
   */
  private void initComboBoxes() {
    customerComboBox.setValue(this.customer);
    contactComboBox.setValue(this.contact);
    userComboBox.setValue(this.user);

    startHourComboBox.setValue(Integer.toString(appointment.getLstrt().toLocalDateTime().getHour()));
    startMinsComboBox.setValue(Integer.toString(appointment.getLstrt().toLocalDateTime().getMinute()));
    endHourComboBox.setValue(Integer.toString(appointment.getLend().toLocalDateTime().getHour()));
    endMinsComboBox.setValue(Integer.toString(appointment.getLend().toLocalDateTime().getMinute()));
  }

  /**
   * Sets Input Fields
   */
  private void initDatePickers(){
    startDatePicker.setValue(appointment.getLstrt().toLocalDateTime().toLocalDate());
    endDatePicker.setValue(appointment.getLend().toLocalDateTime().toLocalDate());
  }

  /**
   * Set times
   */
  private void times() {
    ZoneId estZone = ZoneId.of("America/New_York");

    ObservableList<String> startHrs = FXCollections.observableArrayList();
    ObservableList<String> endHrs = FXCollections.observableArrayList();
    ObservableList<String> startMinutes = FXCollections.observableArrayList();
    ObservableList<String> endMinutes = FXCollections.observableArrayList();

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    LocalDateTime startTime = LocalDateTime.parse("2020-10-31 08:00", formatter);
    LocalDateTime endTime = LocalDateTime.parse("2020-10-31 22:00", formatter);
    ZonedDateTime EstStart = ZonedDateTime.of(startTime, estZone);
    ZonedDateTime EstEnd = ZonedDateTime.of(endTime, estZone);
    ZonedDateTime localStart = EstStart.withZoneSameInstant(ZoneId.systemDefault());
    ZonedDateTime localEnd = EstEnd.withZoneSameInstant(ZoneId.systemDefault());

    for (int i= localStart.getHour(); i <= localEnd.getHour(); i++) {
      if (i<10) {
        startHrs.add("0"+ i);
        endHrs.add("0"+ i);

      } else {
        endHrs.add(Integer.toString(i));
        if (i < localEnd.getHour() && localEnd.getMinute() == 0) {
          startHrs.add(Integer.toString(i));
        }
      }
    }

    startMinutes.addAll("00", "15", "30", "45");
    endMinutes.addAll("00", "15", "30", "45");

    startHourComboBox.setItems(startHrs);
    startMinsComboBox.setItems(startMinutes);
    endHourComboBox.setItems(endHrs);
    endMinsComboBox.setItems(endMinutes);
  }

  /**
   * Fill Combo Boxes
   */
  private void fillComboBoxes() {
    try {
      ResultSet contactsResult = sql("SELECT * FROM contacts");
      ResultSet customersResult = sql("SELECT * FROM customers");
      ResultSet usersResult = sql("SELECT * FROM users");

      this.contactComboBox.getItems().clear();
      this.customerComboBox.getItems().clear();
      this.userComboBox.getItems().clear();

      while (contactsResult.next()) {
        String contact = contactsResult.getString("Contact_Name");
        int id = contactsResult.getInt("Contact_ID");
        contactComboBox.getItems().add(contact);
        if (id == appointment.getCid()) this.contact = contact;
      }
      while (customersResult.next()) {
        String customer = customersResult.getString("Customer_Name");
        int id = customersResult.getInt("Customer_ID");
        this.customerComboBox.getItems().add(customer);
        if (id == appointment.getCuid()) this.customer = customer;
      }
      while (usersResult.next()) {
        String user = usersResult.getString("User_Name");
        int id = usersResult.getInt("User_ID");
        this.userComboBox.getItems().add(user);
        if (id == appointment.getUid()) this.user = user;
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Save updated appointment
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML
  void save(ActionEvent event) throws IOException {
    Timestamp current_time = new Timestamp(new java.sql.Date(System.currentTimeMillis()).getTime());

    int newId = Integer.parseInt(aidTextField.getText());

    try {
      LocalDate st = startDatePicker.getValue();
      LocalDate et = endDatePicker.getValue();

      String tft = titleTextField.getText();
      String dft = descTextField.getText();
      String lft = locationTextField.getText();
      String tyft = typeTextField.getText();
      String shv = startHourComboBox.getValue();
      String smv = startMinsComboBox.getValue();
      String ehv = endHourComboBox.getValue();
      String emv = endMinsComboBox.getValue();
      String lub = session.getUser();
      String cft = contactComboBox.getSelectionModel().getSelectedItem();
      String cift = customerComboBox.getSelectionModel().getSelectedItem();
      String ubft = userComboBox.getSelectionModel().getSelectedItem();

      Timestamp utcStart = Util.tutct(st, shv, smv);
      Timestamp utcEnd = Util.tutct(et, ehv, emv);
      Timestamp lstart = Util.ttlt(st, shv, smv);
      Timestamp lend = Util.ttlt(et, ehv, emv);

      try {
        int cid = 0;
        int cuid = 0;
        int uid = 0;

        ResultSet contactResultSet = sql("SELECT * FROM contacts WHERE Contact_Name = '" + cft + "';");
        ResultSet customerResultSet = sql("SELECT * FROM customers WHERE Customer_Name = '" + cift + "';");
        ResultSet userResultSet = sql("SELECT * FROM users WHERE User_Name = '" + ubft + "';");

        while (contactResultSet.next())
          cid = contactResultSet.getInt("Contact_ID");
        while (customerResultSet.next())
          cuid = customerResultSet.getInt("Customer_ID");
        while (userResultSet.next())
          uid = userResultSet.getInt("User_ID");

        if (Util.checkAppointment(utcStart, utcEnd, cuid, newId, language())) {
          String sqlSTring = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Last_Update = ?, Last_Updated_By = ?, Start = ?, End = ?, Contact_ID = ?, Customer_ID = ?, User_ID = ? WHERE Appointment_ID = ?";
          PreparedStatement psIn = SQL.connect().prepareStatement(sqlSTring);

          psIn.setString(1, tft);
          psIn.setString(2, dft);
          psIn.setString(3, lft);
          psIn.setString(4, tyft);
          psIn.setTimestamp(5, current_time);
          psIn.setString(6, lub);
          psIn.setTimestamp(7, lstart);
          psIn.setTimestamp(8, lend);
          psIn.setInt(9, cid);
          psIn.setInt(10, cuid);
          psIn.setInt(11, uid);
          psIn.setInt(12, newId);

          appointment.setCid(cid);
          appointment.setCuid(cuid);
          appointment.setDescription(dft);
          appointment.setEnd(utcEnd);
          appointment.setFlend(lend);
          appointment.setFlstrt(lstart);
          appointment.setUpdate(current_time);
          appointment.setUpdator(lub);
          appointment.setLend(lend);
          appointment.setLstrt(lstart);
          appointment.setLocation(lft);
          appointment.setTitle(tft);
          appointment.setStart(utcStart);
          appointment.setType(tyft);
          appointment.setUid(uid);

          try (PreparedStatement Statement = psIn) {
            psIn.executeUpdate();
          } catch (Exception e2) {
            e2.printStackTrace();
          }
          loadScreen(event, new AppointmentsScreen(session), "/views/AppointmentScreen.fxml");
        }
      } catch (Exception e) {
          e.printStackTrace();
      }
    }catch (Exception e){
      alert(Alert.AlertType.ERROR
              ,language().getString("ERROR")
              ,language().getString("INVALID_EMPTY"));
    }
  }

  /**
   /**
   * Cancel appointment add
   * @param event Action Event
   * @throws IOException on failure
   */
  @FXML
  void cancel(ActionEvent event) throws IOException {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(language().getString("CANCEL_CONFIRM"));
    alert.setContentText(language().getString("CANCEL_APPOINTMENT_TEXT"));
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK){
      loadScreen(event, new AppointmentsScreen(session), "/views/AppointmentScreen.fxml");
    }
  }
}
