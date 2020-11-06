
package de.samply.share.common.utils;

/**
 * An enum representing an MDR data type.
 */
public enum MdrDatatype {

  /**
   * The Constant DATE.
   */
  DATE("DATE"),

  /**
   * The Constant DATETIME.
   */
  DATETIME("DATETIME"),

  /**
   * The Constant TIME.
   */
  TIME("TIME"),

  /**
   * The Constant STRING.
   */
  STRING("STRING"),

  /**
   * The Constant INTEGER.
   */
  INTEGER("INTEGER"),

  /**
   * The Constant FLOAT.
   */
  FLOAT("FLOAT"),

  /**
   * The Constant BOOLEAN.
   */
  BOOLEAN("BOOLEAN"),

  /**
   * The Constant ENUMERATED.
   */
  ENUMERATED("enumerated"),

  /**
   * The Constant CATALOG.
   */
  CATALOG("CATALOG");


  private final String valididationTypeString;

  MdrDatatype(String valididationTypeString) {
    this.valididationTypeString = valididationTypeString;
  }

  /**
   * Todo.
   * @param value Todo.
   * @return Todo.
   */
  public static MdrDatatype get(String value) {
    for (MdrDatatype validationDatatype : values()) {
      if (validationDatatype.toString().equalsIgnoreCase(value)) {
        return validationDatatype;
      }
    }
    throw new IllegalArgumentException("Value not found: " + value);
  }

  @Override
  public String toString() {
    return valididationTypeString;
  }


}
