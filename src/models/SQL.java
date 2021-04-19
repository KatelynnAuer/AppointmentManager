package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * SQL connection class
 * @author Katelynn Auer
 */
public class SQL {
  public static Connection connection;

  /**
   * SQL DB connection based on values
   * @return SQL connection
   * @throws SQLException exception on failure
   */
  public static Connection connect() throws SQLException {
    return DriverManager.getConnection(Util.URL, Util.username, Util.password);
  }

  /**
   * Close the SQL connection
   * @throws SQLException exception if unsuccessful
   */
  public static void disconnect() throws SQLException {
    connection.close();
  }
}
