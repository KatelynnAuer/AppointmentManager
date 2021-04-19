package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import models.SQL;
import models.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;
import java.sql.PreparedStatement;

/**
 * Appointment Screen Controller
 * @author Katelynn Auer
 */

public class AppointmentsScreen extends TranslatableScreen{
  private final Session session;

  @FXML private Label heading;
  @FXML private Label titleLabel;

  @FXML private Button newAppointmentButton;
  @FXML private Button backButton;
  @FXML private Button removeAppointmentButton;
  @FXML private Button updateAppointmentButton;

  @FXML private ToggleGroup radioToggleGroup;
  @FXML private RadioButton allRadioButton;
  @FXML private RadioButton monthlyRadioButton;
  @FXML private RadioButton weeklyRadioButton;

  @FXML private TableView<Appointment> appTableView;
  @FXML private TableColumn<?, ?> aIdColumn;
  @FXML private TableColumn<?, ?> contactColumn;
  @FXML private TableColumn<?, ?> cUIdColumn;
  @FXML private TableColumn<?, ?> descColumn;
  @FXML private TableColumn<?, ?> endColumn;
  @FXML private TableColumn<?, ?> locationColumn;
  @FXML private TableColumn<?, ?> startColumn;
  @FXML private TableColumn<?, ?> titleColumn;
  @FXML private TableColumn<?, ?> typeColumn;

  /**
   * Appointments Screen Constructor
   * @param session current session
   */
  public AppointmentsScreen(Session session) {
    this.session = session;
  }

  /**
   * This will Initialize the screen
   * @param url fxml url
   * @param resourceBundle Resource Bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    titleLabel.setText("Appointments");
    translateLabels();
    session.getCache().clearCache();
    session.getCache().cache(session.getCache());
    appTableView.setItems(session.getCache().getAppointments());
    initRadios();
  }

  /**
   * Translates the labels to locale
   */
  private void translateLabels() {
    translate(heading, "LOGIN_BANNER");
    translate(aIdColumn, "ID");
    translate(titleColumn, "TITLE");
    translate(descColumn, "DESCRIPTION");
    translate(locationColumn, "LOCATION");
    translate(contactColumn, "CONTACT");
    translate(typeColumn, "TYPE");
    translate(titleLabel, "APPOINTMENTS");
    translate(startColumn, "START_DATE");
    translate(endColumn, "END_DATE");
    translate(cUIdColumn, "CUSTOMER_ID");
    translate(newAppointmentButton, "ADD");
    translate(updateAppointmentButton, "UPDATE");
    translate(removeAppointmentButton, "DELETE");
    translate(backButton, "BACK");
    translate(allRadioButton, "ALL");
    translate(monthlyRadioButton, "WITHIN_MONTH");
    translate(weeklyRadioButton, "WITHIN_WEEK");
  }

  /**
   * View to add an appointment
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML void newAppointment(ActionEvent event) throws IOException {
    loadScreen(event, new NewAppointmentScreen(session), "/views/NewAppointmentScreen.fxml");
  }

  /**
   * View to update selected appointment
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML void updateAppointment(ActionEvent event) throws IOException {
    Appointment selection = appTableView.getSelectionModel().getSelectedItem();
    if (selection != null)
      loadScreen(event, new UpdateAppointmentScreen(session, selection), "/views/UpdateAppointmentScreen.fxml");
    else
      alert(Alert.AlertType.ERROR
              ,language().getString("ERROR")
              ,language().getString("ERROR")
              ,language().getString("APPOINTMENT_SELECT")
      );
  }

  /**
   * View to remove selected appointment
   * @param event Action Event
   * @throws SQLException Exception on failure
   */
  @FXML void deleteAppointment(ActionEvent event) throws SQLException {
    Appointment selection = appTableView.getSelectionModel().getSelectedItem();
    if (selection!= null) {
      ObservableList<Appointment> appointments = FXCollections.observableArrayList();
      appointments = appTableView.getItems();
      String sqlString = "DELETE FROM appointments WHERE Appointment_ID = "
              + appTableView.getSelectionModel().getSelectedItem().getAid();
      try (PreparedStatement statement = SQL.connect().prepareStatement(sqlString)) {
        statement.executeUpdate();
      }
      alert(Alert.AlertType.INFORMATION,
              language().getString("APPOINTMENT_DELETE_TITLE"),
              language().getString("APPOINTMENT_DELETE_TITLE"),
              language().getString("APPOINTMENT_DELETE_TITLE")
                      +": "+ appTableView.getSelectionModel().getSelectedItem().getAid() + " Type: " + appTableView.getSelectionModel().getSelectedItem().getType()
      );

      appointments.remove(appTableView.getSelectionModel().getSelectedItem());
      appTableView.setItems(appointments);
    } else {
      alert(Alert.AlertType.ERROR,
              language().getString("ERROR"),
              language().getString("ERROR"),
              language().getString("APPOINTMENT_NOT_FOUND")
      );
    }
  }

  @FXML void setAllRadios(ActionEvent event) {
    setRadio(true, true);
  }
  @FXML void setMonthlyRadio(ActionEvent event) { setRadio(false, false); }

  @FXML void setWeeklyRadio(ActionEvent event) {
    setRadio(false, true);
  }

  /**
  * Initialize Radio Buttons
   */
  private void initRadios(){
    radioToggleGroup = new ToggleGroup();
    allRadioButton.setToggleGroup(radioToggleGroup);
    weeklyRadioButton.setToggleGroup(radioToggleGroup);
    monthlyRadioButton.setToggleGroup(radioToggleGroup);
    allRadioButton.setSelected(true);
    weeklyRadioButton.setSelected(false);
    monthlyRadioButton.setSelected(false);
  }

  /**
   * Set Radio Buttons
   * @param all Load all
   * @param week weekly or monthly
   */
  private void setRadio(boolean all, boolean week) {
    if (all) appTableView.setItems(session.getCache().getAppointments());
    else{
      LocalDateTime now, end;
      now = LocalDateTime.now();
      end = week ? now.plusWeeks(1): now.plusMonths(1);
      FilteredList<Appointment> data = new FilteredList<>(session.getCache().getAppointments());
      data.setPredicate(row -> {
        LocalDateTime rowDate = row.getLstrt().toLocalDateTime();
        return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(end);
      });
      appTableView.setItems(data);
    }
  }

  /**
   * Functionality to return to main screen
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML void goBack(ActionEvent event) throws IOException { loadMain(event, session);}

}
