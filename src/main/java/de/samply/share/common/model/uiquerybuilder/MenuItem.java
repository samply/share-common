package de.samply.share.common.model.uiquerybuilder;

import de.samply.common.mdrclient.domain.EnumElementType;
import java.util.ArrayList;

/**
 * The Class MenuItem.
 */
public class MenuItem {

  /**
   * The mdr id.
   */
  private String mdrId;

  /**
   * The designation.
   */
  private String designation;

  /**
   * The definition.
   */
  private String definition;

  /**
   * The random uuid.
   */
  private String randomUuid;

  /**
   * The enum element type.
   */
  private EnumElementType enumElementType;

  /**
   * The children.
   */
  private ArrayList<MenuItem> children;

  /**
   * The li style class.
   */
  private String styleClassLi;

  /**
   * The a style class.
   */
  private String styleClassA;

  /**
   * The i style class.
   */
  private String styleClassI;

  /**
   * The parent.
   */
  private MenuItem parent;

  /**
   * (Optional) DKTK Search String for Catalog Entries.
   */
  private String searchString;

  /**
   * Instantiates a new menu item.
   *
   * @param mdrId           the mdr id
   * @param designation     the designation
   * @param definition      the definition
   * @param enumElementType the enum element type
   * @param children        the children
   * @param styleClassLi    the li style class
   * @param styleClassA     the a style class
   * @param styleClassI     the i style class
   * @param parent          the parent
   */
  public MenuItem(String mdrId, String designation, String definition,
      EnumElementType enumElementType,
      ArrayList<MenuItem> children, String styleClassLi, String styleClassA, String styleClassI,
      MenuItem parent) {
    super();
    this.mdrId = mdrId;
    this.designation = designation;
    this.definition = definition;
    this.enumElementType = enumElementType;
    this.children = children;
    this.styleClassLi = styleClassLi;
    this.styleClassA = styleClassA;
    this.styleClassI = styleClassI;
    this.parent = parent;
    this.searchString = null;
  }

  /**
   * Instantiates a new menu item.
   *
   * @param mdrId           the mdr id
   * @param designation     the designation
   * @param definition      the definition
   * @param enumElementType the enum element type
   * @param children        the children
   * @param styleClassLi    the li style class
   * @param styleClassA     the a style class
   * @param styleClassI     the i style class
   * @param parent          the parent
   * @param searchString    the searchString
   */
  public MenuItem(String mdrId, String designation, String definition,
      EnumElementType enumElementType,
      ArrayList<MenuItem> children, String styleClassLi, String styleClassA, String styleClassI,
      MenuItem parent, String searchString) {
    super();
    this.mdrId = mdrId;
    this.designation = designation;
    this.definition = definition;
    this.enumElementType = enumElementType;
    this.children = children;
    this.styleClassLi = styleClassLi;
    this.styleClassA = styleClassA;
    this.styleClassI = styleClassI;
    this.parent = parent;
    this.searchString = searchString;
  }

  /**
   * Gets the mdr id.
   *
   * @return the mdr id
   */
  public String getMdrId() {
    return mdrId;
  }

  /**
   * Sets the mdr id.
   *
   * @param mdrId the new mdr id
   */
  public void setMdrId(String mdrId) {
    this.mdrId = mdrId;
  }

  /**
   * Gets the random uuid.
   *
   * @return the random uuid
   */
  public String getRandomUuid() {
    return randomUuid;
  }

  /**
   * Sets the random uuid.
   *
   * @param randomUuid the new random uuid
   */
  public void setRandomUuid(String randomUuid) {
    this.randomUuid = randomUuid;
  }

  /**
   * Gets the enum element type.
   *
   * @return the enum element type
   */
  public EnumElementType getEnumElementType() {
    return enumElementType;
  }

  /**
   * Sets the enum element type.
   *
   * @param enumElementType the new enum element type
   */
  public void setEnumElementType(EnumElementType enumElementType) {
    this.enumElementType = enumElementType;
  }

  /**
   * Gets the children.
   *
   * @return the children
   */
  public ArrayList<MenuItem> getChildren() {
    return children;
  }

  /**
   * Sets the children.
   *
   * @param children the new children
   */
  public void setChildren(ArrayList<MenuItem> children) {
    this.children = children;
  }

  /**
   * Gets the designation.
   *
   * @return the designation
   */
  public String getDesignation() {
    return designation;
  }

  /**
   * Sets the designation.
   *
   * @param designation the new designation
   */
  public void setDesignation(String designation) {
    this.designation = designation;
  }

  /**
   * Gets the parent.
   *
   * @return the parent
   */
  public MenuItem getParent() {
    return parent;
  }

  /**
   * Sets the parent.
   *
   * @param parent the new parent
   */
  public void setParent(MenuItem parent) {
    this.parent = parent;
  }

  public String getSearchString() {
    return searchString;
  }

  public void setSearchString(String searchString) {
    this.searchString = searchString;
  }

  /**
   * Gets the li style class.
   *
   * @return the li style class
   */
  public String getStyleClassLi() {
    return styleClassLi;
  }

  /**
   * Sets the li style class.
   *
   * @param styleClassLi the new li style class
   */
  public void setStyleClassLi(String styleClassLi) {
    this.styleClassLi = styleClassLi;
  }

  /**
   * Gets the a style class.
   *
   * @return the a style class
   */
  public String getStyleClassA() {
    return styleClassA;
  }

  /**
   * Sets the a style class.
   *
   * @param styleClassA the new a style class
   */
  public void setStyleClassA(String styleClassA) {
    this.styleClassA = styleClassA;
  }

  /**
   * Gets the i style class.
   *
   * @return the i style class
   */
  public String getStyleClassI() {
    return styleClassI;
  }

  /**
   * Sets the i style class.
   *
   * @param styleClassI the new i style class
   */
  public void setStyleClassI(String styleClassI) {
    this.styleClassI = styleClassI;
  }

  /**
   * Gets the definition.
   *
   * @return the definition
   */
  public String getDefinition() {
    return definition;
  }

  /**
   * Sets the definition.
   *
   * @param definition the new definition
   */
  public void setDefinition(String definition) {
    this.definition = definition;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String parentId = "unknown";
    if (parent != null) {
      parentId = parent.getMdrId();
    }
    return "MenuItem [mdrId=" + mdrId + ", designation=" + designation + ", definition="
        + definition + ", randomUuid=" + randomUuid + ", enumElementType="
        + enumElementType + ", children=" + children + ", liStyleClass=" + styleClassLi
        + ", aStyleClass=" + styleClassA + ", iStyleClass="
        + styleClassI + ", parent=" + parentId + "]";
  }
}
