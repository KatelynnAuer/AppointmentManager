package models;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Session Model
 * @author Katelynn Auer
 */

public class Session {

  private String username;
  private Locale locale1;
  final private Cache cache;

  public Session() {
   this.cache = new Cache();

    // Fill the cache on init
    Cache.cache(this.getCache());
    this.locale1 = Locale.getDefault();
  }
  /**
   * get timezone display name in string format
   */
  public String getStringTimezone(){
    return TimeZone.getTimeZone(ZoneId.systemDefault()).getDisplayName();
  }


  /**
   * Add a user to the session
   * @param str user name
   */
  public void newUser(String str) {
    if (str != null)
      username = str;
  }

  /**
   * Get the session user's locale
   * @return locale of the session's user
   */
  public Locale getLocale() {
    return locale1;
  }
  /**
   * Get the session's user's name
   * @return the session's user's name
   */
  public String getUser() {
    return username;
  }

  public Cache getCache() {
    return cache;
  }


}
