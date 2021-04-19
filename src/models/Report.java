package models;

import javafx.beans.value.ObservableValue;

/**
 * Report Model
 * @author Katelynn Auer
 */
public class Report {
  private ObservableValue<String> attrs;
  private ObservableValue<Integer> count;

  /**
   * Reports Constructor
   * @param count Number of appointments
   * @param attrs attribute of interest
   */
  public Report(ObservableValue<Integer> count, ObservableValue<String> attrs) {
    super();
    setAttrs(attrs);
    setCount(count);
  }

  /**
   * Set attributes
   * @param attrs attribute
   */
  public void setAttrs(ObservableValue<String> attrs) {
    this.attrs = attrs;
  }

  /**
   * Set count
   * @param c count
   */
  public void setCount(ObservableValue<Integer> c) {
    this.count = c;
  }

  /**
   * Get attributes
   * @return attributes
   */
  public ObservableValue<String> getAttrs() {
    return attrs;
  }

  /**
   * Get count
   * @return count
   */
  public ObservableValue<Integer> getCount() {
    return count;
  }
}
