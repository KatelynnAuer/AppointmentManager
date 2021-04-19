package models;

/**
 * Contact Model
 * @author Katelynn Auer
 */
public class Contact {
  private int id;
  private String email;
  private String name;

  /**
   * Create contact
   * @param id Contact ID
   * @param name Name of contact
   * @param email Email of contact
   */
  public Contact(int id, String name, String email) {
    super();
    setId(id);
    setName(name);
    setEmail(email);
  }

  /**
   * Set ID
   * @param id id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Set name
   * @param str name
   */
  public void setName(String str) {
    this.name = str;
  }

  /**
   * Set email address
   * @param str email
   */
  public void setEmail(String str) {
    this.email = str;
  }

  /**
   * Get id
   * @return ID
   */
  public int getId() {
    return id;
  }

  /**
   * Get name
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * Get email
   * @return email
   */
  public String getEmail() {
    return email;
  }
}
