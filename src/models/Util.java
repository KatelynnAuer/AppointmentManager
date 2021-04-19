package models;

import javafx.scene.control.Alert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Commonly used methods util class
 * @author Katelynn Auer
 */
public class Util {
   
    // SQL Database settings
    public static final String username="U08Pls";
    public static final String password="53689354351";
    public static final String db ="WJ08Pls";
    public static final String URL ="jdbc:mysql://wgudb.ucertify.com/" + db + "?useSSL=false";
    private static Connection conn;

    public static Connection makeConnection() throws ClassNotFoundException, SQLException, Exception {
        Class.forName(URL);
        conn = (Connection) DriverManager.getConnection(URL, username, password);
        //Lambda expression that prints to the console when user logins 
        new Thread(() -> System.out.println("Connection sucessful!")).start();
        return conn;
        
    }
    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception {
        
        conn.close();
    } 
    
    public static Connection getConnection () {
        return conn;
    }



    /**
    Generic alert method
     @param type alert type
     @param title alert title
     @param header alert header
     */
    public static void alert(Alert.AlertType type, String title, String header){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    /**
     Generic alert method
     @param type alert type
     @param title alert title
     @param header alert header
     @param content alert content
     */
    public static void alert(Alert.AlertType type, String title, String header, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }


   public static DateTimeFormatter date24(boolean ss){
       String datePattern = "yyyy-MM-dd HH:mm" + (ss ? ":ss": "");
       DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(datePattern);
       return dateFormat;
   }

    public static DateTimeFormatter dateKK(){
        String datePattern = "yyyy-MM-dd KK:mm";
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(datePattern);
        return dateFormat;
    }
    public static DateTimeFormatter date12(){
        String datePattern = "yyyy-MM-dd hh:mm";
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(datePattern);
        return dateFormat;
    }
    /**
     * Check if times over-lap
     * @param start Start time
     * @param end End time
     * @param cuid Customer ID
     * @param aid Appointment ID
     * @param language Language resource bundle
     * @return returns false on over-lap
     * @throws SQLException if unsuccessful
     */
    public static boolean checkAppointment(Timestamp start, Timestamp end, int cuid, int aid, ResourceBundle language) throws SQLException {
        //convert times to EST
        ZonedDateTime zustartUTC = ZonedDateTime.of(start.toLocalDateTime(), ZoneOffset.UTC);
        ZonedDateTime zuendUTC = ZonedDateTime.of(end.toLocalDateTime(), ZoneOffset.UTC);
        ZonedDateTime zustart = zustartUTC.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime zuend = zuendUTC.withZoneSameInstant(ZoneId.of("America/New_York"));
        //get EST hours
        LocalDateTime BHstart = LocalDateTime.of(zustart.toLocalDate(), LocalTime.of(8, 0));
        LocalDateTime BHend = LocalDateTime.of(zuend.toLocalDate(), LocalTime.of(22, 0));
        ZonedDateTime BHstart2 = ZonedDateTime.of(BHstart, ZoneId.of("America/New_York"));
        ZonedDateTime BHend2 = ZonedDateTime.of(BHend, ZoneId.of("America/New_York"));
        System.out.println("zustart is : " + zustart + "\nzuend is " + zuend + "\n BHstart2 is " + BHstart2 + "\nBHend2 is" + BHend2);
        System.out.println("zustart is after bhstart2:" + zustart.isAfter(BHstart2) + "\n zustart is before BHend2" + zustart.isBefore(BHend2) + "\n zuend.isBefore(BHend2)" + zuend.isBefore(BHend2) +
        "\nzuend.isAfter(BHstart2))" + zuend.isAfter(BHstart2));

        if (!zustart.isEqual(BHstart2)) {
            if (!zuend.isEqual(BHend2)) {
                if (!zustart.isAfter(BHstart2) || !zustart.isBefore(BHend2) || !zuend.isBefore(BHend2) || !zuend.isAfter(BHstart2)) {

                    alert(Alert.AlertType.ERROR,
                            language.getString("BUSINESS HOURS"),
                            language.getString("BUSINESS_HOURS_TITLE"),
                            language.getString("BUSINESS_HOURS_TEXT")
                    );
                    return false;

                }
            }
        }
        if (!checkTimes(start, end, cuid, aid)) {
            alert(Alert.AlertType.ERROR,
                    language.getString("SCHEDULE_CONFLICT"),
                    language.getString("CUSTOMER_TIME_CONFLICT")
            );
            return false;
        }
        return true;
    }

    /**
     * Generate new element ID
     * @param table SQL table
     * @param property SQL property
     * @return id
     * @throws SQLException on failure
     */
    public static int newId(String table, String property) throws SQLException {
        String sql = "SELECT * FROM "+table;
        Statement stmt = SQL.connect().createStatement();
        ResultSet result = stmt.executeQuery(sql);
        int c = 0;
        while (result.next()) {
            if (result.getInt(property) > c) c = result.getInt(property);
        }
        return c + 1;
    }

    /**
     * Query a SQL string
     * @param sqlString SQL string to query
     * @return ResultSet Result set of query
     * @throws SQLException Exception on failure
     */
    public static ResultSet sql(String sqlString) throws SQLException {
        Statement statement = SQL.connect().createStatement();
        return statement.executeQuery(sqlString);
    }

    /**
     * check available time slot
     * @param utcend utc end time
     * @param utcstart utc start time
     * @param cuid linked customer
     * @param aid appointment id
     * @return false on no times
     * @throws SQLException if unsuccessful
     */
    public static boolean checkTimes(Timestamp utcstart, Timestamp utcend, int cuid, int aid) throws SQLException {
        ZonedDateTime start1 = ZonedDateTime.of(utcstart.toLocalDateTime(), ZoneId.of("America/New_York"));
        ZonedDateTime end1 = ZonedDateTime.of(utcend.toLocalDateTime(), ZoneId.of("America/New_York"));

        ResultSet utcResult =  sql("SELECT * FROM appointments WHERE Customer_ID = '"+cuid+"' AND Appointment_ID != '"+aid+"';");
        String s = "", e = "";
        while (utcResult.next()) {
            s = utcResult.getString("Start");
            e = utcResult.getString("End");

            ZonedDateTime start2 = ZonedDateTime.of(Timestamp.valueOf(s).toLocalDateTime(), ZoneId.of("America/New_York"));
            ZonedDateTime end2 = ZonedDateTime.of(Timestamp.valueOf(e).toLocalDateTime(), ZoneId.of("America/New_York"));

            if (start1.isBefore(end2) && end1.isAfter(start2) || start1.isEqual(start2) || end2.isEqual(end1))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Convert to local time
     * @param time UTC
     * @param h hour
     * @param m mins
     * @return Timestamp time
     */
    public static Timestamp ttlt(LocalDate time, String h, String m) {
        LocalDateTime l = LocalDateTime.of(time.getYear(), time.getMonthValue(), time.getDayOfMonth(), Integer.parseInt(h), Integer.parseInt(m));
        return Timestamp.valueOf(l);
    }
    /**
     * Convert to local to UTC
     * @param local local time
     * @param h hour
     * @param m mins
     * @return Timestamp time
     */
    public static Timestamp tutct(LocalDate local, String h, String m) {
        LocalTime z = LocalTime.of(Integer.parseInt(h), Integer.parseInt(m));
        LocalDateTime t = LocalDateTime.of(local, z);
        ZonedDateTime zt = t.atZone(ZoneId.systemDefault());
        ZonedDateTime ztc = zt.withZoneSameInstant(ZoneOffset.UTC);
        System.out.println("ZonedDateTime Here before conversion" + zt);

        System.out.println("ZonedDateTime Here after conversion" + ztc);
        System.out.println("timestamp here:" + Timestamp.valueOf(ztc.toLocalDateTime()));
        return Timestamp.valueOf(ztc.toLocalDateTime());
    }
}
