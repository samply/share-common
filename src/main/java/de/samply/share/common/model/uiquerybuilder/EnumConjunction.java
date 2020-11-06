package de.samply.share.common.model.uiquerybuilder;

/**
 * Holds the possible conjunctions used in conjunction groups.
 */
public enum EnumConjunction {
  AND("and"),
  OR("or");

  private String name;

  /**
   * Instantiates a new enum conjunction.
   *
   * @param name the name
   */
  private EnumConjunction(String name) {
    this.setName(name);
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
}
