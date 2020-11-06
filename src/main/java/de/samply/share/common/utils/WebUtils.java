package de.samply.share.common.utils;

/**
 * The Class WebUtils.
 */
public final class WebUtils {

  public static String getVersionOfDataelement(String mdrId) {
    return mdrId.substring(mdrId.lastIndexOf(':') + 1);
  }

  public static MdrIdDatatype getMdrIdDatatype(String urn) {
    return new MdrIdDatatype(urn);
  }

}
