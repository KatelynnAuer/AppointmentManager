package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import models.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Session;
import models.Customer;
import models.SQL;

import static models.Util.sql;

/**
 * New Customer Screen controller
 * @author Katelynn Auer
 */
public class NewCustomerScreen extends TranslatableScreen{
  private final Session session;

  private final List<String> countries = new ArrayList<>();
  private final List<String> divisions = new ArrayList<>();

  @FXML private Button cancelCustomerButton;
  @FXML private Button saveCustomerButton;

  @FXML private ComboBox<String> countryComboBox;
  @FXML private ComboBox<String> divisionComboBox;

  @FXML private Label heading;
  @FXML private Label addressLabel;
  @FXML private Label countryLabel;
  @FXML private Label cuidLabel;
  @FXML private Label customerNameLabel;
  @FXML private Label divisionBoxLabel;
  @FXML private Label headTitle;
  @FXML private Label phoneLabel;
  @FXML private Label zipLabel;

  @FXML private TextField addressTextField;
  @FXML private TextField cuidTextField;
  @FXML private TextField nameTextField;
  @FXML private TextField phoneTextField;
  @FXML private TextField zipTextField;

  /**
   * Add Customer Screen Constructor
   * @param session current session
   */
  public NewCustomerScreen(Session session) {
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
    initComboBoxes();
  }

  /**
   * Translate the labels to locale
   */
  private void translateLabels() {
    translate(heading, "LOGIN_BANNER");
    translate(cuidLabel, "ID");
    translate(customerNameLabel, "NAME");
    translate(addressLabel, "ADDRESS");
    translate(zipLabel, "ZIP_CODE");
    translate(phoneLabel, "PHONE");
    translate(countryLabel, "COUNTRY");
    translate(divisionBoxLabel, "DIVISON");
    translate(cuidTextField, "DISABLED");
    translate(nameTextField, "CUSTOMER_NAME");
    translate(addressTextField, "ADDRESS");
    translate(zipTextField, "ZIP_CODE");
    translate(phoneTextField, "PHONE");
    translate(saveCustomerButton, "SAVE");
    translate(cancelCustomerButton, "CANCEL");
    translate(headTitle, "NEW_CUSTOMER");
  }


  /**
   * Initialize Combo Boxes
   */
  private void initComboBoxes() {
      clear();
    try {
      ResultSet countriesSet = sql("SELECT Country FROM countries");
      ResultSet divisionsSet = sql("SELECT Division FROM first_level_divisions d");
      while (countriesSet.next()) {
        String country = countriesSet.getString("Country");
        this.countryComboBox.getItems().add(country);
        this.countries.add(country);
      }
      while (divisionsSet.next()) {
        String division = divisionsSet.getString("Division");
        this.divisionComboBox.getItems().add(division);
        this.divisions.add(division);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Clear Combo Boxes and lists
   */
  private void clear() {
    this.countryComboBox.getItems().clear();
    this.divisionComboBox.getItems().clear();
    this.countries.clear();
    this.divisions.clear();
  }


  /**
   * Update Combo Boxes
   * @param event Action Event
   */
  @FXML
  void setComboBoxes(ActionEvent event) {
    try {
      ResultSet countryResultSet = sql("SELECT d.Division_ID, d.Division, d.COUNTRY_ID, c.Country_ID, c.Country FROM first_level_divisions d JOIN countries c ON (d.COUNTRY_ID = c.Country_ID) WHERE c.Country = '"+ countryComboBox.getValue()+"';");
      this.divisionComboBox.getItems().removeAll(this.divisions);
      this.divisions.clear();
      while (countryResultSet.next()) {
        String division = countryResultSet.getString("Division");
        this.divisionComboBox.getItems().add(division);
        this.divisions.add(division);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Cancel customer add
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML
  void cancel(ActionEvent event) throws IOException {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(language().getString("CANCEL_CONFIRM"));
    alert.setContentText(language().getString("CANCEL_CONFIRM_TEXT"));
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK)
        loadScreen(event, new CustomersScreen(session), "/views/CustomerScreen.fxml");
  }

  /**
   * Save appointment
   * @param event Action event
   * @throws SQLException Exception sql on failure
   * @throws IOException Exception on failure
   */
  @FXML
  void save(ActionEvent event) throws SQLException, IOException {
    int newId = Util.newId("customers", "Customer_ID");

    java.sql.Date cDate = new java.sql.Date(System.currentTimeMillis());
    Timestamp cTime = new Timestamp( new java.sql.Date(System.currentTimeMillis()).getTime());

    String cnt = nameTextField.getText();
    String at = addressTextField.getText();
    String pct = zipTextField.getText();
    String pt = phoneTextField.getText();
    String creator = session.getUser();
    String updater = session.getUser();
    String dt = divisionComboBox.getSelectionModel().getSelectedItem();
    String ct = countryComboBox.getSelectionModel().getSelectedItem();

    if (cnt.equals("") || at.equals("") || pct.equals("") || pt.equals("") || cDate == null ||
            creator.equals("") || cDate == null || updater.equals("") || dt == null || ct.equals("")) {
      alert(Alert.AlertType.ERROR,
              language().getString("ERROR"),
              language().getString("INVALID_EMPTY")
      );
    }
    else {
      Customer customer = new Customer(newId, cnt, at, pct, pt, cDate, creator, cTime, updater, dt, ct);
      try {
        ResultSet divisionResultSet = sql("SELECT * FROM first_level_divisions WHERE Division = '" + dt + "';");
        int dId = 0;
        while (divisionResultSet.next()) {
          dId = divisionResultSet.getInt("Division_ID");
        }
        String sqlString = "INSERT INTO customers VALUES(?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement psIn = SQL.connect().prepareStatement(sqlString);

        psIn.setInt(1, newId);
        psIn.setString(2, cnt);
        psIn.setString(3, at);
        psIn.setString(4, pct);
        psIn.setString(5, pt);
        psIn.setDate(6, cDate);
        psIn.setString(7, creator);
        psIn.setTimestamp(8, cTime);
        psIn.setString(9, updater);
        psIn.setInt(10, dId);

        try (PreparedStatement statement = psIn) {
          psIn.executeUpdate();
        }catch(Exception e){
          e.printStackTrace();
          alert(Alert.AlertType.ERROR,
                  language().getString("ERROR"),
                  language().getString("INVALID_CUSTOMER_TITLE"),
                  language().getString("INVALID_CUSTOMER_TEXT")
          );
        }
        session.getCache().newCustomer(customer);
      } catch (Exception e) {
        e.printStackTrace();
      }
      loadScreen(event, new CustomersScreen(session), "/views/CustomerScreen.fxml");
    }
  }
}
