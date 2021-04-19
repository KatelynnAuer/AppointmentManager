package controllers;

import models.Appointment;
import models.SQL;
import models.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Customer Screen Controller
 * @author Katelynn Auer
 */
public class CustomersScreen extends TranslatableScreen{

  private final Session session;

  @FXML private Button newCustomerButton;
  @FXML private Button updateCustomerButton;
  @FXML private Button deleteCustomerButton;
  @FXML private Button backButton;

  @FXML private Label heading;
  @FXML private Label banner;

  @FXML private TableView<Customer> customerTableView;
  @FXML private TableColumn<?, ?> countryTableColumn;
  @FXML private TableColumn<?, ?> addressTableColumn;
  @FXML private TableColumn<?, ?> cuidTableColumn;
  @FXML private TableColumn<?, ?> customerNameTableColumn;
  @FXML private TableColumn<?, ?> didTableColumn;
  @FXML private TableColumn<?, ?> phoneNumberTableColumn;
  @FXML private TableColumn<?, ?> zipTableColumn;

  /**
   * Customer Screen Constructor
   * @param session current session
   */
  public CustomersScreen(Session session) { this.session = session; }

  /**
   * Initializes the screen
   * @param url fxml url
   * @param resourceBundle Resource Bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    translateLabels();
    
    customerTableView.setItems(session.getCache().getCustomers());
  }
  /**
   * Shows label in proper language
   */
  private void translateLabels() {
    translate(cuidTableColumn, "ID");
    translate(customerNameTableColumn, "NAME");
    translate(addressTableColumn, "ADDRESS");
    translate(zipTableColumn, "ZIP_CODE");
    translate(phoneNumberTableColumn, "PHONE");
    translate(didTableColumn, "DIVISON");
    translate(countryTableColumn, "COUNTRY");
    translate(newCustomerButton, "ADD");
    translate(updateCustomerButton, "UPDATE");
    translate(deleteCustomerButton, "DELETE");
    translate(backButton, "BACK");
    translate(heading, "LOGIN_BANNER");
    translate(banner, "CUSTOMERS");
  }

  /**
   * View to add a customer
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML
  void newCustomer(ActionEvent event) throws IOException {
    loadScreen(event, new NewCustomerScreen(session), "/views/NewCustomerScreen.fxml");
  }

  /**
   * Update selected appointment
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML
  private void updateCustomer(ActionEvent event) throws IOException {
    Customer selection = customerTableView.getSelectionModel().getSelectedItem();
    if (selection != null) {
      loadScreen(event, new UpdateCustomerScreen(session, selection), "/views/UpdateCustomerScreen.fxml");
    } else {
      alert(Alert.AlertType.ERROR
              ,language().getString("ERROR")
              ,language().getString("ERROR")
              ,language().getString("CUSTOMER_SELECT")
      );
    }
  }

  /**
   * Allows you to remove selected appointment
   * @param event Action Event
   * @throws SQLException Exception on failure
   */
  @FXML
  void deleteCustomer(ActionEvent event) throws SQLException{
    Customer selection = customerTableView.getSelectionModel().getSelectedItem();
    if (selection!= null) {
     
      ObservableList<Customer> customers = FXCollections.observableArrayList();
      customers = customerTableView.getItems();
      String sqlString = "DELETE FROM customers WHERE Customer_ID = " + selection.getCid();
      String sqlString2 = "DELETE FROM appointments WHERE Customer_ID = " + selection.getCid();
      Statement statement = SQL.connect().createStatement();
      statement.executeUpdate(sqlString2);
      statement.executeUpdate(sqlString);
 
      ObservableList<Appointment> apptsCache = session.getCache().getAppointments();
      for (Appointment appt : apptsCache){
          System.out.println(appt.getTitle());
          if (appt.getCuid() == selection.getCid()){
              apptsCache.remove(appt);
              break;
          }
      }
      customers.remove(customerTableView.getSelectionModel().getSelectedItem());
     
      customerTableView.setItems(customers);
      alert(Alert.AlertType.INFORMATION, language().getString("DELETE_CUSTOMER_TITLE"), language().getString("DELETE_CUSTOMER_TITLE"));
    }
     else {
      alert(Alert.AlertType.ERROR,
              language().getString("ERROR"),
              language().getString("ERROR"),
              language().getString("CUSTOMER_NOT_FOUND")
      );
    }
  }

  /**
   * Allows you to return to main screen
   * @param event Action Event
   * @throws IOException Exception on failure
   */
  @FXML void goBack(ActionEvent event) throws IOException { loadMain(event, session);}

  private boolean customerHasAppointments(Customer customer){
  int appointments = 0;
      for( Appointment appointment : session.getCache().getAppointments()){
    if (appointment.getCuid() == customer.getCid()) appointments++;
  };
      return appointments > 0;
}
}
