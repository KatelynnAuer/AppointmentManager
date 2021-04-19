package models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Cache Model
 * @author Katelynn Auer
 */

public class Cache {
     private ObservableList<Customer> customers;
     private ObservableList<Contact> contacts;
     private ObservableList<Appointment> appointments;

    public Cache() {
        customers = FXCollections.observableArrayList();
        contacts = FXCollections.observableArrayList();
        appointments = FXCollections.observableArrayList();
    }

    /**
     * Add a new appointment
     * @param a new appointment
     */
    
    public void clearCache(){
     customers = FXCollections.observableArrayList();
     contacts = FXCollections.observableArrayList();
     appointments = FXCollections.observableArrayList();
    }
    public void newAppointment(Appointment a) {
        if (a != null)
            appointments.add(a);
    }
    /**
     * Add a new customer
     * @param c new customer
     */
    public void newCustomer(Customer c) {
        if (c != null)
            customers.add(c);
    }
    /**
     * Add a new contact
     * @param c new contact
     */
    public void newContact(Contact c) {
        if (c != null)
            contacts.add(c);
    }

    /**
     * Get the cached customers
     * @return list of customers
     */
    public ObservableList<Customer> getCustomers() {
        return customers;
    }
    /**
     * Get the cached contacts
     * @return list of customers
     */
    public ObservableList<Contact> getContacts() {
        return contacts;
    }
    /**
     * Get the cached appointments
     * @return list of appointments
     */
    public ObservableList<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * Cache data
     * @param ca cache
     */
  public static void cache(Cache ca) {
        try {
            String cstring = "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, c.Phone, c.Create_Date, c.Created_By, c.Last_Update, c.Last_Updated_By, c.Division_ID, d.Division, n.Country FROM customers c JOIN first_level_divisions d ON (c.Division_ID = d.Division_ID) JOIN countries n ON (d.COUNTRY_ID = n.Country_ID);";
            try {
                PreparedStatement psin = SQL.connect().prepareStatement(cstring);
                 ResultSet result = psin.executeQuery();
                while (result.next()) {
                    int id = result.getInt("c.Customer_ID");
                    Timestamp l = result.getTimestamp("c.Last_Update");
                    Date e = result.getDate("c.Create_Date");
                    String n = result.getString("c.Customer_Name");
                    String a = result.getString("c.Address");
                    String z = result.getString("c.Postal_Code");
                    String p = result.getString("c.Phone");
                    String c = result.getString("c.Created_By");
                    String u = result.getString("c.Last_Updated_By");
                    String d = result.getString("d.Division");
                    String y = result.getString("n.Country");
                    Customer customer = new Customer(id, n, a, z, p, e, c, l, u, d, y);
                    ca.newCustomer(customer);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String astring = "SELECT * FROM appointments a JOIN contacts c ON (a.Contact_ID = c.Contact_ID) JOIN customers m ON (a.Customer_ID = m.Customer_ID) JOIN users u ON (a.User_ID = u.User_ID);";
            try {
            PreparedStatement psin = SQL.connect().prepareStatement(astring);
                 ResultSet result = psin.executeQuery();
                while (result.next()) {
                    int i = result.getInt("a.Appointment_ID");
                    int ci = result.getInt("a.Contact_ID");
                    int cui = result.getInt("a.Customer_ID");
                    int ui = result.getInt("a.User_ID");
                    Timestamp st = result.getTimestamp("a.Start");
                    Timestamp en = result.getTimestamp("a.End");
                    Timestamp cd = result.getTimestamp("a.Create_Date");
                    Timestamp lu = result.getTimestamp("a.Last_Update");
                    String t = result.getString("a.Title");
                    String de = result.getString("a.Description");
                    String lc = result.getString("a.Location");
                    String ty = result.getString("a.Type");
                    String cr = result.getString("a.Created_By");
                    String lur = result.getString("a.Last_Updated_By");
                    Appointment appointment = new Appointment(i, t, de, lc, ty, ci, st, en, cd, cr, lu, lur, cui, ui);
                    ca.newAppointment(appointment);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            String costring = "SELECT * FROM contacts c;";
            try {
            PreparedStatement psin = SQL.connect().prepareStatement(costring);
                 ResultSet result = psin.executeQuery();
                while (result.next()) {
                    int i = result.getInt("c.Contact_ID");
                    String n = result.getString("c.Contact_Name");
                    String e = result.getString("c.Email");
                    Contact contact = new Contact(i, n, e);
                    ca.newContact(contact);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
