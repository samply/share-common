package de.samply.share.common.utils;

import java.util.regex.PatternSyntaxException;

public class MdrIdDatatype {

  public static final String SEPARATOR = ":";
  public static final String LATEST_VERSION_WILDCARD_MDR = "latest";
  public static final String LATEST_VERSION_WILDCARD_CENTRAXX = "*";
  private static final String URN = "urn";

  private String namespace;
  private String datatype;
  private String id;
  private String version;

  /**
   * Todo.
   * @param urn Todo.
   */
  public MdrIdDatatype(String urn) {
    if (!urn.startsWith(URN)) {
      throw new IllegalArgumentException("wrong prefix: " + urn);
    }
    try {
      String[] urnTokens = urn.split(SEPARATOR);
      if (urnTokens.length < 4 || urnTokens.length > 5) {
        throw new IllegalArgumentException("Invalid amount of tokens: " + urn);
      }
      this.namespace = urnTokens[1];
      this.datatype = urnTokens[2];
      this.id = urnTokens[3];
      if (urnTokens.length > 4) {
        this.version = urnTokens[4];
      } else {
        this.version = "";
      }
    } catch (PatternSyntaxException pse) {
      throw new IllegalArgumentException("Pattern Syntax Exception.", pse);
    } catch (NumberFormatException nfe) {
      throw new IllegalArgumentException("Number Format Exception.", nfe);
    }
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getDatatype() {
    return datatype;
  }

  public void setDatatype(String datatype) {
    this.datatype = datatype;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getVersion() {
    return version;
  }

  /**
   * Todo.
   * @param version Todo.
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Todo.
   * @return Todo.
   */
  public String getMajor() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(URN).append(SEPARATOR)
        .append(namespace).append(SEPARATOR)
        .append(datatype).append(SEPARATOR)
        .append(id);

    return stringBuffer.toString();
  }

  /**
   * Todo.
   * @return Todo.
   */
  public String getLatestMdr() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(URN).append(SEPARATOR)
        .append(namespace).append(SEPARATOR)
        .append(datatype).append(SEPARATOR)
        .append(id).append(SEPARATOR)
        .append(LATEST_VERSION_WILDCARD_MDR);

    return stringBuffer.toString();
  }

  /**
   * Todo.
   * @return Todo.
   */
  public String getLatestCentraxx() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(URN).append(SEPARATOR)
        .append(namespace).append(SEPARATOR)
        .append(datatype).append(SEPARATOR)
        .append(id).append(SEPARATOR)
        .append(LATEST_VERSION_WILDCARD_CENTRAXX);

    return stringBuffer.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MdrIdDatatype that = (MdrIdDatatype) o;

    if (!namespace.equals(that.namespace)) {
      return false;
    }
    if (!datatype.equals(that.datatype)) {
      return false;
    }
    if (!id.equals(that.id)) {
      return false;
    }
    return version != null ? version.equals(that.version) : that.version == null;
  }

  /**
   * Todo.
   * @param o Todo.
   * @return Todo.
   */
  public boolean equalsIgnoreVersion(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MdrIdDatatype that = (MdrIdDatatype) o;

    if (!namespace.equals(that.namespace)) {
      return false;
    }
    if (!datatype.equals(that.datatype)) {
      return false;
    }
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    int result = namespace.hashCode();
    result = 31 * result + datatype.hashCode();
    result = 31 * result + id.hashCode();
    result = 31 * result + (version != null ? version.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(URN).append(SEPARATOR)
        .append(namespace).append(SEPARATOR)
        .append(datatype).append(SEPARATOR)
        .append(id).append(SEPARATOR)
        .append(version);

    return stringBuffer.toString();
  }

}
