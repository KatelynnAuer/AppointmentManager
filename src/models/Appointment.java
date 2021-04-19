package models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import static models.Util.*;

/**
 * Appointments Model
 * @author Katelynn Auer
 */
public class Appointment {
  private int aid;
  private int cid;
  private int cuid;
  private int uid;
  private String creator;
  private String description;
  private String fLend;
  private String fLstrt;
  private String updator;
  private String location;
  private String title;
  private String type;
  private Timestamp init;
  private Timestamp end;
  private Timestamp update;
  private Timestamp lend;
  private Timestamp lstrt;
  private Timestamp start;
  

  /**
   * Appointment Constructor
   * @param aid Appointment ID
   * @param title Appointment Title
   * @param description Appointment description
   * @param location Appointment location
   * @param type Appointment Type
   * @param cid Appointment's Contact ID
   * @param start Appointment Start Time
   * @param end Appointment End Time
   * @param init Appointiment initial creation date
   * @param creator Appointment creator
   * @param update Appointment last update
   * @param updator Appointment updater
   * @param cuid Customer ID
   * @param uid User ID
   */
  public Appointment(int aid, String title, String description, String location,
                     String type, int cid, Timestamp start, Timestamp end,
                     Timestamp init, String creator, Timestamp update, String updator,
                     int cuid, int uid) {
    super();
    setAid(aid);
    setTitle(title);
    setDescription(description);
    setLocation(location);
    setType(type);
    setCid(cid);
    setStart(start);
    setEnd(end);
    setLstrt(start);
    setFlstrt(start);
    setLend(end);
    setFlend(end);
    setInit(init);
    setCreator(creator);
    setUpdate(update);
    setUpdator(updator);
    setCuid(cuid);
    setUid(uid);
  }

  /**
   * Creates a new appointment.
   * @param title title
   * @param desc description
   * @param location location
   * @param type type
   * @param lend local end time
   * @param lstrt local start time
   * @param date date
   * @param cid contact id
   * @param utcend utc end time
   * @param utcstart utc start time
   * @param creator creator
   * @param update last update time
   * @param cuid customer id
   * @param aid appointment id
   * @param uid user id
   * @return Newly added appointment
   * @throws SQLException exception
   */
  public static Appointment cApp(int aid, String title, String desc, String location, String type,
                                 Timestamp lend, Timestamp lstrt, Timestamp date, int cid,
                                 Timestamp utcstart, Timestamp utcend, String creator, String update,
                                 int cuid, int uid) throws SQLException {
    String sqlString = "INSERT INTO appointments VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    PreparedStatement psin = SQL.connect().prepareStatement(sqlString);
    psin.setInt(1, aid);
    psin.setString(2, title);
    psin.setString(3, desc);
    psin.setString(4, location);
    psin.setString(5, type);
    psin.setTimestamp(6, lstrt);
    psin.setTimestamp(7, lend);
    psin.setTimestamp(8, date);
    psin.setString(9, creator);
    psin.setTimestamp(10, date);
    psin.setString(11, update);
    psin.setInt(12, cuid);
    psin.setInt(13, 1);
    psin.setInt(14, cid);
    try {
      PreparedStatement ps = psin;
      ps.executeUpdate();
    } catch (SQLException throwables) {
      throwables.printStackTrace();
    }
    Appointment appointment = new Appointment(
            aid, title, desc, location, type, cid,
            utcstart, utcend, date, creator, date, update,
            cuid, uid);

    appointment.setLstrt(lstrt);
    appointment.setLend(lend);
    appointment.setFlstrt(lstrt);
    appointment.setFlend(lend);
    return appointment;
  }

  /**
   * Set Appointment ID
   * @param id id value
   */
  public void setAid(int id) { this.aid = id; }

  /**
   * Set Appointment title
   * @param str title value
   */
  public void setTitle(String str) {
    this.title = str;
  }

  /**
   * Set Appointment description
   * @param str description value
   */
  public void setDescription(String str) {
    this.description = str;
  }

  /**
   * Set Appointment location
   * @param str location value
   */
  public void setLocation(String str) {
    this.location = str;
  }

  /**
   * Set Appointment type
   * @param str type value
   */
  public void setType(String str) {
    this.type = str;
  }

  /**
   * Set Appointment start time
   * @param time start time value
   */
  public void setStart(Timestamp time) {
    this.start = time;
  }

  /**
   * Set start time
   * @param time start time value
   */
  public void setLstrt(Timestamp time) {
    this.lstrt = time;
  }

  /**
   * Set start time
   * @param time start time value
   */
  public void setFlstrt(Timestamp time) {
    this.fLstrt = time.toLocalDateTime().format(date12());
  }

  /**
   * Set end time
   * @param time end time value
   */
  public void setEnd(Timestamp time) {
    this.end = time;
  }

  /**
   * Set end time
   * @param time end time value
   */
  public void setLend(Timestamp time) {
    this.lend = time;
  }

  /**
   * Set end time
   * @param time end time value
   */
  public void setFlend(Timestamp time) {
    this.fLend = time.toLocalDateTime().format(date12());
  }

  /**
   * Set initial create time
   * @param time initial create time value
   */
  public void setInit(Timestamp time) {
    this.init = time;
  }

  /**
   * Set appointment creator
   * @param str creator value
   */
  public void setCreator(String str) { this.creator = str; }

  /**
   * Set appointment update date/time
   * @param time update time value
   */
  public void setUpdate(Timestamp time) {
    this.update = time;
  }

  /**
   * Set appointment updater
   * @param str updater value
   */
  public void setUpdator(String str) {
    this.updator = str;
  }

  /**
   * Set customer id
   * @param id customer id value
   */
  public void setCuid(int id) {
    this.cuid = id;
  }

  /**
   * Set user id
   * @param id user id value
   */
  public void setUid(int id) { this.uid = id; }

  /**
   * Set contact id
   * @param id contact id value
   */
  public void setCid(int id) {
    this.cid = id;
  }

  /**
   * Get appointment ID
   * @return appointment ID
   */
  public int getAid() {
    return aid;
  }

  /**
   * Get appointment title
   * @return appointment title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Get appointment description
   * @return appointment descrption
   */
  public String getDescription() {
    return description;
  }

  /**
   * Get appointment location
   * @return appiontment location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Get appointment type
   * @return appointment type
   */
  public String getType() {
    return type;
  }

  /**
   * Get appointment start time
   * @return appointment start time
   */
  public Timestamp getStart() {
    return start;
  }

  /**
   * Get appointment start time
   * @return appointment start time
   */
  public Timestamp getLstrt() {
    return lstrt;
  }

  /**
   * Get appointment start time
   * @return appointment start time
   */
  public String getfLstrt() {
    return fLstrt;
  }

  /**
   * Get appointment end time
   * @return appointment end time
   */
  public Timestamp getEnd() {
    return end;
  }

  /**
   * Get appointment end time
   * @return appointment end time
   */
  public Timestamp getLend() {
    return lend;
  }

  /**
   * Get appointment end time
   * @return appointment end time
   */
  public String getfLend() {
    return fLend;
  }

  /**
   * Get appointment initial create time
   * @return appointment create  time
   */
  public Timestamp getInit() {
    return init;
  }

  /**
   * Get appointment initial creator
   * @return appointment creator
   */
  public String getCreator() {
    return creator;
  }

  /**
   * Get appointment update time
   * @return update time
   */
  public Timestamp getUpdate() {
    return update;
  }

  /**
   * Get appointment updater
   * @return updater value
   */
  public String getUpdator() {
    return updator;
  }

  /**
   * Get the user ID
   * @return user ID
   */
  public int getUid() {
    return uid;
  }

  /**
   * Get the contact ID
   * @return contact ID
   */
  public int getCid() {
    return cid;
  }

  /**
   * Get the customer ID
   * @return customer ID
   */
  public int getCuid() {
    return cuid;
  }
}
