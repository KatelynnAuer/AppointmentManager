package controllers;

import models.Session;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static models.Util.dateKK;
import static models.Util.sql;

/**
 * Login Screen Controller
 * @author Katelynn Auer
 */
public class LoginScreen extends TranslatableScreen {

  private final Session session;
  private String alertContent = "";

  @FXML
  private Label heading;
  @FXML
  private Label userNameLabel;
  @FXML
  private Label passwordLabel;
  @FXML
  private Label localeLabel;

  @FXML
  private PasswordField passwordTextField;
  @FXML
  private TextField userNameTextField;

  @FXML
  private Button loginButton;
  @FXML
  private Button exitButton;

  /**
   * Initialize login page with locale
   *
   * @param url            fxml url
   * @param languageBundle Resource Bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle languageBundle) {
    translateLabels();
    localeLabel.setText(this.session.getStringTimezone());
  }

  /**
   * Translate the labels to locale
   */
  private void translateLabels() {
    translate(heading, "LOGIN_BANNER");
    translate(userNameLabel, "USERNAME");
    translate(passwordLabel, "PASSWORD");
    translate(userNameTextField, "USERNAME");
    translate(passwordTextField, "PASSWORD");
    translate(loginButton, "LOGIN");
    translate(exitButton, "EXIT");
  }

  /**
   * Login Screen Constructor
   *
   * @param session current session
   */
  public LoginScreen(Session session) {
    this.session = session;
  }

  /**
   * Login to database
   *
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML
  void login(ActionEvent event) throws IOException {

    String userName, password;
    userName = userNameTextField.getText();
    password = passwordTextField.getText();
    if (validate(userName, password)) {
      if (!checkForAppointments(15))
        alert(Alert.AlertType.INFORMATION, language().getString("NO_APPOINTMENTS_TITLE"),
                language().getString("NO_APPOINTMENTS_TITLE"),
                language().getString("NO_APPOINTMENTS") + "\n");

      log("User "+ userName + " Successful login\n");
      session.newUser(userName);
      loadMain(event, session);
    } else {
      log("User "+ userName + " Failed login\n");
      alert(Alert.AlertType.WARNING,
              language().getString("INVALID_LOGIN_TITLE"),
              language().getString("INVALID_LOGIN_TITLE"),
              language().getString("INVALID_LOGIN"));
    }
  }

  /**
   * Validate user against database
   *
   * @param userName user's username
   * @param password user's password
   * @return false on failure
   */
  private boolean validate(String userName, String password) {
    boolean check = false;
    try {
      ResultSet result = sql("SELECT * FROM users WHERE User_Name = '" + userName + "';");
      while (result.next()) {
        String user = result.getString("User_Name");
        String pass = result.getString("Password");
        check = user.matches(userName) && password.matches(pass);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return check;
  }

  /**
   * Check for appointments
   * First Lambda: This lambda expression will check if an appointment is within 15 minutes
   * Second Lambda: This lambda expression will prove an alert per appointment
   * @param delayMinutes range to check within
   * @return false on no appointments
   */
  private boolean checkForAppointments(int delayMinutes) {
    LocalDateTime now, delay;

    now = LocalDateTime.now();
    System.out.println(now);
    delay = now.plusMinutes(delayMinutes);

    FilteredList<Appointment> appointments = new FilteredList<>(session.getCache().getAppointments());

    // Fist Lambda
    appointments.setPredicate(row -> {
      LocalDateTime dr = row.getLstrt().toLocalDateTime();
      return dr.isAfter(now.minusMinutes(1)) && dr.isBefore(delay);
    });

    if (!appointments.isEmpty()) {
      StringBuilder alertContent = new StringBuilder(language().getString("APPOINTMENT_ALERT_TEXT") + "\n");
      // Fist Lambda
      appointments.forEach(appointment -> {
        System.out.println(appointment.getAid());
        alertContent.append(language().getString("APPOINTMENT")).append(" ")
                .append(appointment.getAid()).append(": ")
                .append(appointment.getLstrt().toLocalDateTime()
                        .format(dateKK()))
                .append(" \n");
      });
      alert(Alert.AlertType.INFORMATION, language().getString("APPOINTMENT_ALERT_TITLE"),
              language().getString("APPOINTMENT_ALERT_TITLE"),
              alertContent.toString());
      return true;
    }
    return false;
  }

  /**
   * Close the program
   *
   * @param event Action Event
   */
  @FXML
  void exit(ActionEvent event) {
    exitProgram(event);
  }

  /**
   * Log to login_activity.txt file
   * @param log data to log
   * @throws FileNotFoundException Exception on failure
   */
  private void log(String log) throws FileNotFoundException {
    File file = new File("login_activity.txt");
    PrintWriter pw = new PrintWriter(new FileOutputStream(file , true));
    Timestamp time = new Timestamp(new Date(System.currentTimeMillis()).getTime());
    pw.append(time +": " +log);
    pw.close();
  }
}
