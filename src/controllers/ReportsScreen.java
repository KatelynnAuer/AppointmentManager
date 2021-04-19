package controllers;

import java.io.IOException;
import java.net.URL;
import java.time.format.TextStyle;
import java.util.*;

import models.Contact;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Appointment;
import models.Session;
import models.Report;

/**
 * Reports Screen Controller
 * @author Katelynn Auer
 */
public class ReportsScreen extends TranslatableScreen{
  private final Session session;
  private final List<String> contacts = new ArrayList<>();
  private ObservableList<Report> months;
  private ObservableList<Report> types;
  private ObservableList<Report> locations;

  @FXML private Tab typeAndMonthTab;
  @FXML private Tab ScheduleTab;
  @FXML private Tab LocationTab;

  @FXML private TableView<Report> monthTableView;
  @FXML private TableView<Appointment> appointmentsTableView;
  @FXML private TableColumn<Appointment, Date> cuidTableColumn;
  @FXML private TableColumn<Appointment, Date> endTableColumn;
  @FXML private TableColumn<Appointment, Date> startTableColumn;
  @FXML private TableColumn<Appointment, Integer> aidTableColumn;
  @FXML private TableColumn<Appointment, Integer> titleTableColumn;
  @FXML private TableColumn<Appointment, String> descriptionTableColumn;
  @FXML private TableColumn<Appointment, String> typeTableColumn;

  @FXML private TableView<Report> typeTableView;
  @FXML private TableView<Report> locationTableView;
  @FXML private TableColumn<Report, Integer> rTypeTotalTableColumn;
  @FXML private TableColumn<Report, String> rTypeTableColumn;
  @FXML private TableColumn<Report, Integer> rTypeTotal2TableColumn;
  @FXML private TableColumn<Report, String> rMonthTableColumn;
  @FXML private TableColumn<Report, Integer> rTypeLocationTableColumn;
  @FXML private TableColumn<Report, String> rLocationTableColumn;

  @FXML private Button backButton;
  @FXML private ComboBox<String> contactBoxIn;

  @FXML private Label heading;
  @FXML private Label headTitle;

  /**
   * Reports Screen Constructor
   * @param session current session
   */
  public ReportsScreen(Session session) {
    this.session = session;
  }

  /**
   * Initialize the screen
   * @param url fxml url
   * @param resourceBundle Resource Bundle
   */
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    translateElements();
    initMonths();
    initTypes();
    initLocations();
    initContacts();
  }

  /**
   * Translate the elements to locale
   */
  private void translateElements (){
    translate(heading, "LOGIN_BANNER");
    translate(typeAndMonthTab, "TYPE");
    translate(ScheduleTab, "CONTACT");
    translate(LocationTab, "LOCATION");
    translate(aidTableColumn, "ID");
    translate(titleTableColumn, "TITLE");
    translate(descriptionTableColumn, "DESCRIPTION");
    translate(typeTableColumn, "TYPE");
    translate(startTableColumn, "START_DATE");
    translate(endTableColumn, "END_DATE");
    translate(cuidTableColumn, "CUSTOMER_ID");
    translate(rTypeTotalTableColumn, "TOTAL");
    translate(rTypeTableColumn, "TYPE");
    translate(rMonthTableColumn, "MONTH");
    translate(backButton, "BACK");
    translate(headTitle, "REPORTS");
  }

  /**
   * Load contacts
   */
  private void initContacts() {
    clear();
    contactBoxIn.getItems().add(language().getString("ALL"));
    contactBoxIn.setValue(language().getString("ALL"));
    for(Contact contact: session.getCache().getContacts()){
      contactBoxIn.getItems().add(contact.getName());
      this.contacts.add(contact.getName());
    }
    appointmentsTableView.setItems(session.getCache().getAppointments());
  }

  private void clear(){
    this.contactBoxIn.getItems().clear();
    this.contacts.clear();
  }

  /**
   * Initialize locations
   */
  private void initTypes() {
    String type = "";
    int c;
    types = FXCollections.observableArrayList();
    Map<String, Integer> typem = new HashMap<String, Integer>();
    for (Appointment appointment :session.getCache().getAppointments()) {
      type = appointment.getType();
      c = 0;
      if (!typem.containsKey(type)) {
        for (int j = 0; j < session.getCache().getAppointments().size(); j++) {
          if (session.getCache().getAppointments().get(j).getType().equals(type)) {
            c++;
          }
        }
        typem.put(type, c);
      }
    }
    for (Map.Entry<String, Integer> map : typem.entrySet()) {
      types.add(new Report(
              new ReadOnlyObjectWrapper<>(map.getValue()),
              new ReadOnlyStringWrapper(map.getKey())
      ));
    }
    rTypeTotalTableColumn.setCellValueFactory(cell -> {
      return cell.getValue().getCount();
    });
    rTypeTableColumn.setCellValueFactory(cell -> {
      return cell.getValue().getAttrs();
    });
    typeTableView.setItems(types);
  }

  /**
   * Initialize Locations
   */
  private void initLocations() {
    String location = "";
    int c;
    locations = FXCollections.observableArrayList();
    Map<String, Integer> locationm = new HashMap<String, Integer>();
    for (Appointment appointment :session.getCache().getAppointments()) {
      location = appointment.getLocation();
      c = 0;
      if (!locationm.containsKey(location)) {
        for (int j = 0; j < session.getCache().getAppointments().size(); j++) {
          if (session.getCache().getAppointments().get(j).getLocation().equals(location)) {
            c++;
          }
        }
        locationm.put(location, c);
      }
    }
    for (Map.Entry<String, Integer> map : locationm.entrySet()) {
      locations.add(new Report(
              new ReadOnlyObjectWrapper<>(map.getValue()),
              new ReadOnlyStringWrapper(map.getKey())
      ));
    }
    rTypeLocationTableColumn.setCellValueFactory(cell -> {
      return cell.getValue().getCount();
    });
    rLocationTableColumn.setCellValueFactory(cell -> {
      return cell.getValue().getAttrs();
    });
    locationTableView.setItems(locations);
  }

  /**
   * Initialize Lists
   */

  private void initMonths() {
    String m = "";
    months = FXCollections.observableArrayList();
    Map<String, Integer> types = new HashMap<String, Integer>();
    for (Appointment appointment: session.getCache().getAppointments()){
      Locale locale = Locale.getDefault();
      m = appointment.getLstrt().toLocalDateTime().getMonth().getDisplayName(TextStyle.FULL, locale);
      if (!types.containsKey(m)) {
        ArrayList<String> tl = new ArrayList<>()  ;
        for (Appointment j: session.getCache().getAppointments()){
          if (j.getLstrt().toLocalDateTime().getMonth().getDisplayName(TextStyle.FULL, locale).equals(m)) {
            if (!tl.contains(j.getType())) {
              tl.add(j.getType());
            }
          }
        }
        types.put(m, tl.size());
      }
    }
    for (Map.Entry<String, Integer> map : types.entrySet()) {
      months.add(new Report(
              new ReadOnlyObjectWrapper<>(map.getValue()),
              new ReadOnlyStringWrapper(map.getKey())
      ));
    }
    rMonthTableColumn.setCellValueFactory(cellData -> {
      return cellData.getValue().getAttrs();
    });
    rTypeTotal2TableColumn.setCellValueFactory(cellData -> {
      return cellData.getValue().getCount();
    });
    monthTableView.setItems(months);
  }

  /**
   * Combo Box toggle
   * @param event Action Event
   */
  @FXML
  void toggleContact(ActionEvent event) {
    String selection = contactBoxIn.getSelectionModel().getSelectedItem();
    FilteredList<Appointment> result = new FilteredList<>(session.getCache().getAppointments());
    result.setPredicate(row -> {
      Integer cid = row.getCid();
      return selection.equals("All") || selection.equals(session.getCache().getContacts().get(cid - 1).getName());
    });
    if (selection.equals("All")) appointmentsTableView.setItems(session.getCache().getAppointments());
    else appointmentsTableView.setItems(result);
  }

  /**
   * Go back to main screen
   * @param event Action Event
   * @throws IOException on failure
   */
  @FXML
  void goBack(ActionEvent event) throws IOException{
    loadMain(event, session);
  }
}
