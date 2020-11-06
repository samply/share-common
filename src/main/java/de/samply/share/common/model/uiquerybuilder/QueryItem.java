package de.samply.share.common.model.uiquerybuilder;

import de.samply.share.model.common.MultivalueAttribute;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class QueryItem.
 */
public class QueryItem implements Serializable {

  /**
   * The Constant serialVersionUID.
   */
  private static final long serialVersionUID = 1L;
  /**
   * The Constant logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(QueryItem.class);
  /**
   * The id.
   */
  protected int id;
  /**
   * This attribute represents whether the primitive attribute id is null.
   */
  protected boolean idNull = true;
  /**
   * The mdr id.
   */
  protected java.lang.String mdrId;
  /**
   * The operator.
   */
  protected EnumOperator operator;
  /**
   * The conjunction.
   */
  protected EnumConjunction conjunction;
  /**
   * The value.
   */
  protected java.lang.String value;
  /**
   * The lower bound.
   */
  protected java.lang.String lowerBound;
  /**
   * The upper bound.
   */
  protected java.lang.String upperBound;
  /**
   * The values.
   */
  protected List<String> values;
  /**
   * A boolean value, indicating if this is the root item and shall not be deletable.
   */
  protected boolean isRoot;
  /**
   * Temporary id, for the view, to enable dragging, dropping and sorting elements.
   */
  private String tempId = UUID.randomUUID().toString();

  /**
   * Method 'QueryItem'.
   */
  public QueryItem() {
    isRoot = false;
  }

  /**
   * Method 'getId'.
   *
   * @return int
   */
  public int getId() {
    return id;
  }

  /**
   * Method 'setId'.
   *
   * @param id the new id
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Gets the value of idNull.
   *
   * @return true, if is id null
   */
  public boolean isIdNull() {
    return idNull;
  }

  /**
   * Sets the value of idNull.
   *
   * @param idNull the new id null
   */
  public void setIdNull(boolean idNull) {
    this.idNull = idNull;
  }

  /**
   * Method 'getMdrId'.
   *
   * @return java.lang.String
   */
  public java.lang.String getMdrId() {
    return mdrId;
  }

  /**
   * Method 'setMdrId'.
   *
   * @param mdrId the new mdr id
   */
  public void setMdrId(java.lang.String mdrId) {
    this.mdrId = mdrId;
  }

  /**
   * Gets the operator.
   *
   * @return the operator
   */
  public EnumOperator getOperator() {
    return operator;
  }

  /**
   * Sets the operator.
   *
   * @param operator the new operator
   */
  public void setOperator(EnumOperator operator) {
    this.operator = operator;
  }

  /**
   * Gets the conjunction.
   *
   * @return the conjunction
   */
  public EnumConjunction getConjunction() {
    return conjunction;
  }

  /**
   * Sets the conjunction.
   *
   * @param conjunction the new conjunction
   */
  public void setConjunction(EnumConjunction conjunction) {
    this.conjunction = conjunction;
  }

  /**
   * Method 'getValue'.
   *
   * @return java.lang.String
   */
  public java.lang.String getValue() {
    return value;
  }

  /**
   * Method 'setValue'.
   *
   * @param value the new value
   */
  public void setValue(java.lang.String value) {
    this.value = value;
  }

  /**
   * Gets the lower bound.
   *
   * @return the lower bound
   */
  public java.lang.String getLowerBound() {
    return lowerBound;
  }

  /**
   * Sets the lower bound.
   *
   * @param lowerBound the new lower bound
   */
  public void setLowerBound(java.lang.String lowerBound) {
    this.lowerBound = lowerBound;
  }

  /**
   * Gets the upper bound.
   *
   * @return the upper bound
   */
  public java.lang.String getUpperBound() {
    return upperBound;
  }

  /**
   * Sets the upper bound.
   *
   * @param upperBound the new upper bound
   */
  public void setUpperBound(java.lang.String upperBound) {
    this.upperBound = upperBound;
  }

  /**
   * Gets the values.
   *
   * @return the values
   */
  public List<String> getValues() {
    return values;
  }

  /**
   * Sets the values.
   *
   * @param values the new values
   */
  public void setValues(List<String> values) {
    this.values = values;
  }

  public boolean isRoot() {
    return isRoot;
  }

  public void setRoot(boolean isRoot) {
    this.isRoot = isRoot;
  }

  /**
   * Gets the temp id.
   *
   * @return the temp id
   */
  public String getTempId() {
    return tempId;
  }

  /**
   * Sets the temp id.
   *
   * @param tempId the new temp id
   */
  public void setTempId(String tempId) {
    this.tempId = tempId;
  }

  /**
   * Sets the in.
   *
   * @param mva the new in
   */
  public void setIn(MultivalueAttribute mva) {
    values = new ArrayList<>();
    if (mva.getValue() == null || mva.getValue().size() < 1) {
      logger.error("No values attached");
    } else {
      for (JAXBElement<String> je : mva.getValue()) {
        values.add(je.getValue());
      }
    }
  }

  @Override
  public String toString() {
    return "QueryItem{"
        + "tempId='" + tempId + '\''
        + ", id=" + id
        + ", idNull=" + idNull
        + ", mdrId='" + mdrId + '\''
        + ", operator=" + operator
        + ", conjunction=" + conjunction
        + ", value='" + value + '\''
        + ", lowerBound='" + lowerBound + '\''
        + ", upperBound='" + upperBound + '\''
        + ", values=" + values
        + ", isRoot=" + isRoot
        + '}';
  }
}
