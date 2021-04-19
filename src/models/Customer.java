package models;

import java.util.Date;
import java.sql.Timestamp;

/**
 * Customer Model
 * @author Katelynn Auer
 */
public class Customer {
  private Date create;
  private int cid;
  private String address;
  private String country;
  private String creator;
  private String name;
  private String division;
  private String closer;
  private String phone;
  private String zip;
  private Timestamp close;

  /**
   * Customer Constructor
   * @param cid Customer ID
   * @param name Customer Name
   * @param address Customer Address
   * @param zip Customer ZIP Code
   * @param phone Customer Phone Number
   * @param create Customer creation date
   * @param creator Creator of customer
   * @param closer Last updater of customer
   * @param close Last update
   * @param division Customer division
   * @param country Customer country
   */
  public Customer(int cid, String name, String address, String zip, String phone, Date create,
                  String creator, Timestamp close, String closer, String division, String country) {
    super();
    setId(cid);
    setName(name);
    setAddress(address);
    setZip(zip);
    setPhone(phone);
    setCreate(create);
    setCreatedBy(creator);
    setClose(close);
    setCloser(closer);
    setDivision(division);
    setCountry(country);
  }
  /**
   * Set Customer ID
   * @param id id
   */
  public void setId(int id) {
    this.cid = id;
  }
  /**
   * Set Customer name
   * @param str name
   */
  public void setName(String str) {
    this.name = str;
  }
  /**
   * Set Customer address
   * @param str address
   */
  public void setAddress(String str) {
    this.address = str;
  }
  /**
   * Set customer ZIP code
   * @param str ZIP code
   */
  public void setZip(String str) {
    this.zip = str;
  }
  /**
   * Set customer phone number
   * @param str Phone number
   */
  public void setPhone(String str) {
    this.phone = str;
  }
  /**
   * Set creation date
   * @param date Creation date
   */
  public void setCreate(Date date) {
    this.create = date;
  }
  /**
   * Sets the customer's creator
   * @param str Name of creator
   */
  public void setCreatedBy(String str) {
    this.creator = str;
  }
  /**
   * Sets customer's update time
   * @param time Update date and time
   */
  public void setClose(Timestamp time) { this.close = time; }
  /**
   * Sets updater
   * @param str Updater
   */
  public void setCloser(String str) {
    this.closer = str;
  }
  /**
   * Sets customer's division
   * @param str Division
   */
  public void setDivision(String str) {
    this.division = str;
  }
  /**
   * Sets customer's country
   * @param str Country
   */
  public void setCountry(String str) {
    this.country = str;
  }
  /**
   * Get the customer's ID
   * @return ID
   */
  public int getCid() {
    return cid;
  }
  /**
   * Get the cumstomer's name
   * @return name
   */
  public String getName() {
    return name;
  }
  /**
   * Gets address
   * @return address
   */
  public String getAddress() {
    return address;
  }
  /**
   * Gets postal code
   * @return postal code
   */
  public String getZip() {
    return zip;
  }
  /**
   * Gets phone number
   * @return phone number
   */
  public String getPhone() {
    return phone;
  }
  /**
   * Gets the date and time customer was created
   * @return date and time
   */
  public Date getCreate() {
    return create;
  }
  /**
   * Gets user
   * @return User that customer was created by
   */
  public String getCreator() {
    return creator;
  }
  /**
   * Gets date and time customer was updated
   * @return date and time
   */
  public Timestamp getLastUpdate() {
    return close;
  }
  /**
   * Get the cumstomer's closer
   * @return user
   */
  public String getCloser() {
    return closer;
  }
  /**
   * Get the cumstomer's division
   * @return division
   */
  public String getDivision() {
    return division;
  }
  /**
   * Get the cumstomer's country
   * @return country
   */
  public String getCountry() {
    return country;
  }
}
