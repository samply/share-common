package de.samply.share.common.model.uiquerybuilder;

/**
 * Holds all possible Operators to be used in the where clause (and sub-clauses) of a query.
 */
public enum EnumOperator {
  IS_NOT_NULL("Is not null", "IS NOT NULL"),
  IS_NULL("Is null", "IS NULL"),
  LIKE("Like", "~"),
  NOT_EQUAL_TO("Not equal to", "≠"),
  LESS_THEN("Less than", "<"),
  LESS_OR_EQUAL_THEN("Less or equal than", "≤"),
  EQUAL("Equals", "="),
  GREATER_OR_EQUAL("Greater or equal than", "≥"),
  GREATER("Greater than", ">"),
  BETWEEN("Between", "BETWEEN"),
  IN("In", "IN");

  private String name;
  private String icon;

  /**
   * Instantiates a new enum operator.
   *
   * @param name the name
   */
  private EnumOperator(String name, String icon) {
    this.setName(name);
    this.setIcon(icon);
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name.
   *
   * @param name the new name
   */
  public void setName(String name) {
    this.name = name;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

}
