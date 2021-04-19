package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

import models.SQL;
import models.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Customer;
import java.sql.PreparedStatement;

import static models.Util.sql;

/**
 * Update Customer Screen Controller
 * @author Katelynn Auer
 */
public class UpdateCustomerScreen extends TranslatableScreen{
  private final Session session;
  private final Customer customer;

  private final List<String> countries = new ArrayList<>();
  private final List<String> divisions = new ArrayList<>();

  @FXML private Label heading;
  @FXML private Label banner;
  @FXML private Label addressLabel;
  @FXML private Label countryLabel;
  @FXML private Label cuidLabel;
  @FXML private Label nameLabel;
  @FXML private Label divisionLabel;
  @FXML private Label phoneLabel;
  @FXML private Label zipLabel;

  @FXML private TextField addressTextField;
  @FXML private TextField cuidTextField;
  @FXML private TextField nameTextField;
  @FXML private TextField phoneTextField;
  @FXML private TextField zipTextField;

  @FXML private ComboBox<String> countryComboBox;
  @FXML private ComboBox<String> divisionComboBox;

  @FXML private Button cancelButton;
  @FXML private Button saveButton;

  /**
   * Appointments Screen Constructor
   * @param session current session
   * @param customer selected customer to update
   */
  public UpdateCustomerScreen(Session session, Customer customer) {
    this.session = session;
    this.customer = customer;
  }

  /**
   * Initialize the screen
   * @param url fxml url
   * @param resourceBundle Resource Bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    translate();
    initTextFields();
    initComboBoxes();
  }

  /**
   * Translate the labels to locale
   */
  private void translate() {
    translate(heading, "LOGIN_BANNER");
    translate(banner, "UPDATE");
    translate(addressTextField, "ADDRESS");
    translate(addressLabel, "ADDRESS");
    translate(cancelButton, "CANCEL");
    translate(countryLabel, "COUNTRY");
    translate(cuidTextField, "DISABLED");
    translate(cuidLabel, "ID");
    translate(nameTextField, "CUSTOMER_NAME");
    translate(nameLabel, "NAME");
    translate(divisionLabel, "DIVISON");
    translate(phoneTextField, "PHONE");
    translate(phoneLabel, "PHONE");
    translate(zipTextField, "ZIP_CODE");
    translate(zipLabel, "ZIP_CODE");
    translate(saveButton, "SAVE");
  }

  private void initTextFields() {
    addressTextField.setText(customer.getAddress());
    countryComboBox.setValue(customer.getCountry());
    cuidTextField.setText(Integer.toString(customer.getCid()));
    nameTextField.setText(customer.getName());
    divisionComboBox.setValue(customer.getDivision());
    phoneTextField.setText(customer.getPhone());
    zipTextField.setText(customer.getZip());
  }

  private void initComboBoxes() {
    clear();
    try {
      ResultSet countryResult = sql("SELECT Country_ID, Country FROM countries");
      ResultSet divisionResult = sql("SELECT Division_ID, Division FROM first_level_divisions d");
      while (countryResult.next()) {
        String country = countryResult.getString("Country");
        this.countryComboBox.getItems().add(country);
        this.countries.add(country);
      }
      while (divisionResult.next()) {
        String division = divisionResult.getString("Division");
        this.divisionComboBox.getItems().add(division);
        this.divisions.add(division);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void clear(){
    this.countryComboBox.getItems().clear();
    this.countries.clear();
  }

  /**
   * Save customer
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML
  void save(ActionEvent event) throws IOException {
    int cid = Integer.parseInt(cuidTextField.getText());
    Timestamp cTime = new Timestamp( new Date().getTime());

    String name = nameTextField.getText();
    String address = addressTextField.getText();
    String zip = zipTextField.getText();
    String phone = phoneTextField.getText();
    String updater = session.getUser();
    String dt = divisionComboBox.getSelectionModel().getSelectedItem();
    String ct = countryComboBox.getSelectionModel().getSelectedItem();

    if (name.equals("") || address.equals("") || zip.equals("")
            || phone.equals("") || updater.equals("") || dt.equals("") || ct.equals(""))
      alert(Alert.AlertType.ERROR,
              language().getString("ERROR"),
              language().getString("INVALID_EMPTY")
      );

    else {
      try {
        int did = 0;
        ResultSet divisionRequest = sql("SELECT * FROM first_level_divisions WHERE Division = '" + dt + "';");
        while (divisionRequest.next()) {
          did = divisionRequest.getInt("Division_ID");
        }
        String sqlString = "UPDATE customers SET Customer_Name = ?, ADDRESS = ?, Phone = ?, Postal_Code = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement psIn = SQL.connect().prepareStatement(sqlString);

        psIn.setString(1, name);
        psIn.setString(2, address);
        psIn.setString(3, phone);
        psIn.setString(4, zip);
        psIn.setTimestamp(5, cTime);
        psIn.setString(6, updater);
        psIn.setInt(7, did);
        psIn.setInt(8, cid);

        customer.setId(cid);
        customer.setName(name);
        customer.setAddress(address);
        customer.setZip(zip);
        customer.setPhone(phone);
        customer.setClose(cTime);
        customer.setCloser(updater);
        customer.setDivision(dt);
        customer.setCountry(ct);

        try (PreparedStatement Statement = psIn) {
          psIn.executeUpdate();
        }
        loadScreen(event, new CustomersScreen(session),"/views/CustomerScreen.fxml");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Update
   * @param event Action Event
   */
  @FXML
  private void update(ActionEvent event) {
    String selectCountry = countryComboBox.getValue();
    this.divisionComboBox.getItems().removeAll(this.divisions);
    this.divisions.clear();
    String sql = "SELECT d.Division_ID, d.Division, d.COUNTRY_ID, c.Country_ID, c.Country FROM first_level_divisions d JOIN countries c ON (d.COUNTRY_ID = c.Country_ID) WHERE c.Country = '"+selectCountry+"';";
    try {
      Statement statement = SQL.connect().prepareStatement(sql);
      ResultSet resultSet = statement.executeQuery(sql);
      while (resultSet.next()) {
        String division = resultSet.getString("Division");
        this.divisionComboBox.getItems().add(division);
        this.divisions.add(division);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Cancel update of customer
   * @param event Action Event
   * @throws IOException on failure
   */
  @FXML
  private void cancel(ActionEvent event) throws IOException {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle(language().getString("CANCEL_CONFIRM"));
    alert.setContentText(language().getString("CANCEL_CONFIRM_TEXT"));
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK){
      loadScreen(event, new CustomersScreen(session), "/views/CustomerScreen.fxml");
    }
  }
}
