
package de.samply.share.common.model.dto;

public class SiteInfo {

  private Integer id;
  private String name;
  private String nameExtended;
  private String description;
  private String contact;
  private boolean approved;

  /**
   * Get the id.
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  /**
   * Set the Id.
   * @param id the id to set
   */
  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Get the name.
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name.
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the extended name.
   * @return the nameExtended
   */
  public String getNameExtended() {
    return nameExtended;
  }

  /**
   * Set the extended name.
   * @param nameExtended the nameExtended to set
   */
  public void setNameExtended(String nameExtended) {
    this.nameExtended = nameExtended;
  }

  /**
   * Get the description.
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the description.
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Get the contact.
   * @return the contact
   */
  public String getContact() {
    return contact;
  }

  /**
   * Set the contact.
   * @param contact the contact to set
   */
  public void setContact(String contact) {
    this.contact = contact;
  }

  /**
   * Check if the site is approved.
   * @return the approved
   */
  public boolean isApproved() {
    return approved;
  }

  /**
   * Set approved parameter.
   * @param approved the approved to set
   */
  public void setApproved(boolean approved) {
    this.approved = approved;
  }
}
