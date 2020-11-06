package de.samply.share.common.utils;

import de.samply.common.mdrclient.domain.DataElement;
import de.samply.common.mdrclient.domain.EnumElementType;
import de.samply.common.mdrclient.domain.Result;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpHost;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collection of utility methods.
 */
public class SamplyShareUtils {

  /**
   * The Constant Alphanumerics.
   */
  static final String ALPHANUMERICS =
      "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final Logger logger = LoggerFactory.getLogger(SamplyShareUtils.class);
  private static final String[] AVAILABLE_FORMATS = {"dd.MM.yyyy", "MM.yyyy", "yyyy"};
  private static final String[] AVAILABLE_PATTERNS = {"\\d\\d\\.\\d\\d\\.\\d\\d\\d\\d",
      "\\d\\d\\.\\d\\d\\d\\d", "\\d\\d\\d\\d"};
  /**
   * A cryptographically strong random number generator.
   */
  static Random random = new SecureRandom();

  /**
   * Avoid instantiation.
   */
  private SamplyShareUtils() {

  }

  /**
   * Basic check for email address validity. at least one @ at least one character in the local part
   * at least one dot in the domain part at least four characters in the domain (assuming that
   * no-one has an address at the tld, that the tld is at least 2 chars.
   *
   * @param address the address to check
   * @return true, if it is a valid email
   */
  public static boolean isEmail(String address) {
    int pos = address.lastIndexOf("@");
    return pos > 0 && (address.lastIndexOf(".") > pos) && (address.length() - pos > 4);
  }

  /**
   * Gets the auth code from the authorization header.
   *
   * @param header the authorization header from the http message
   * @param prefix the expected prefix
   * @return the extracted auth code
   */
  public static String getAuthCodeFromHeader(String header, String prefix) {
    if (header == null || !header.startsWith(prefix)) {
      return null;
    }
    try {
      if (prefix.endsWith(" ")) {
        return header.substring(prefix.length());
      } else {
        return header.substring(prefix.length() + 1);
      }
    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  public static String getFilesuffixFromContentDisposition(String contentDisposition) {
    return getFilesuffixFromFilename(getFilenameFromContentDisposition(contentDisposition));

  }

  /**
   * Todo.
   *
   * @param filename Todo.
   * @return Todo.
   */
  public static String getFilesuffixFromFilename(String filename) {
    if (filename.indexOf('.') > 0) {
      return filename.substring(filename.lastIndexOf('.'));
    } else {
      logger.error("Could not get file suffix from " + filename);
      return "unknown";
    }
  }

  /**
   * Todo.
   *
   * @param contentDisposition Todo.
   * @return Todo.
   */
  public static String getFilenameFromContentDisposition(String contentDisposition) {

    if (contentDisposition == null) {
      logger.error("No Content-Disposition Header found");
      return "unnamed";
    }

    int start;
    start = contentDisposition.indexOf("filename");
    if (start < 0) {
      logger.error("No filename attribute found in Content-Disposition Header");
      return "unnamed";
    } else {
      start += 10;
    }
    int end;
    end = contentDisposition.indexOf("\"", start);
    if (end < 0) {
      logger.error("Error finding the end of the filename attribute in Content-Disposition Header");
      return "unnamed";
    }
    String filename = contentDisposition.substring(start, end);
    return FilenameUtils.getName(filename);
  }

  /**
   * Get the current time as sql timestamp.
   *
   * @return the current time as sql timestamp
   */
  public static Timestamp getCurrentSqlTimestamp() {
    Date nowDate = new Date();
    return new Timestamp(nowDate.getTime());
  }

  public static Time getCurrentTime() {
    Time now = new Time(new Date().getTime());
    return now;
  }

  public static java.sql.Date getCurrentDate() {
    java.sql.Date nowDate = new java.sql.Date(new Date().getTime());
    return nowDate;
  }

  /**
   * Get a diff between two dates.
   *
   * @param date1    the oldest date
   * @param date2    the newest date
   * @param timeUnit the unit in which you want the diff
   * @return the diff value, in the provided unit
   */
  public static long getDateDiff(java.sql.Date date1, java.sql.Date date2, TimeUnit timeUnit) {
    // Just using date1 without making the apparently useless valueof leads to false results since
    // date1 also has time information.
    java.sql.Date d1 = java.sql.Date.valueOf(date1.toString());
    long diffInMillies = date2.getTime() - d1.getTime();
    return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
  }

  /**
   * Adds the trailing file separator.
   *
   * @param in the in
   * @return the string
   */
  public static String addTrailingFileSeparator(String in) {
    String matchExpression;
    if (File.separator.equals("\\")) {
      matchExpression = File.separator + File.separator + "+$";
    } else {
      matchExpression = File.separator + "+$";
    }
    return in.replaceAll(matchExpression, "") + File.separator;
  }

  /**
   * Return the input string with exactly one trailing slash, removing surplus slashes.
   *
   * @param in the input string
   * @return the string with one trailing slash
   */
  public static String addTrailingSlash(String in) {
    if (in == null) {
      return "";
    }
    return in.replaceAll("/+$", "") + "/";
  }

  /**
   * Combine a base url and any number of paths. Avoid issues with leading and/or trailing slashes
   * in urls and paths Similar to java.nio.file.Paths.get(), but keeping the double slash after the
   * scheme (e.g. "https://").
   *
   * @param baseUrl The base url
   * @param path    any number of paths to append
   * @return Sanitized, combined URL
   */
  public static String combineUrl(String baseUrl, String... path) {
    if (baseUrl == null) {
      logger.debug("baseUrl was null. Setting to empty String.");
      baseUrl = "";
    }
    StringBuilder stringBuilder = new StringBuilder(baseUrl.replaceAll("/+$", ""));
    for (String p : path) {
      stringBuilder.append("/");
      stringBuilder.append(p.replaceAll("/+$", "").replaceAll("^/+", ""));
    }
    return stringBuilder.toString();
  }

  /**
   * Compares two version strings. Use this instead of String.compareTo() for a non-lexicographical
   * comparison that works for version strings. e.g. "1.10".compareTo("1.6").
   *
   * @param str1 a string of ordinal numbers separated by decimal points.
   * @param str2 a string of ordinal numbers separated by decimal points.
   * @return The result is a negative integer if str1 is _numerically_ less than str2. The result is
   *        a positive integer if str1 is _numerically_ greater than str2. The result is zero if the
   *        strings are _numerically_ equal.
   */
  public static Integer versionCompare(String str1, String str2) {
    String[] vals1 = str1.split("\\.");
    String[] vals2 = str2.split("\\.");
    int i = 0;
    // set index to first non-equal ordinal or length of shortest version
    // string
    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
      i++;
    }
    // compare first non-equal ordinal number
    if (i < vals1.length && i < vals2.length) {
      int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
      return Integer.signum(diff);
    } else {
      // the strings are equal or one string is a substring of the other
      // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
      return Integer.signum(vals1.length - vals2.length);
    }
  }

  /**
   * Extracts the version number of samply share client from the user agent header.
   *
   * @param userAgentHeader the user agent header as received via http request.
   * @return The version number (sans eventual snapshot suffixes) of the user agent null if it could
   *        not be found
   */
  public static String getVersionFromUserAgentHeader(String userAgentHeader) {
    // User-Agent: Samply.Share/1.1.2-SNAPSHOT
    if (userAgentHeader == null || !userAgentHeader.startsWith("Samply.Share")) {
      logger.debug("Unknown client: " + userAgentHeader);
      return null;
    }
    int slashIndex = userAgentHeader.indexOf("/");
    if (slashIndex < 0) {
      return null;
    }
    int hyphenIndex = userAgentHeader.indexOf("-", slashIndex);

    if (hyphenIndex > 0) {
      return userAgentHeader.substring(slashIndex + 1, hyphenIndex);
    } else {
      int spaceIndex = userAgentHeader.indexOf(" ", slashIndex);
      if (spaceIndex > 0) {
        return userAgentHeader.substring(slashIndex + 1, spaceIndex);
      }
    }
    return userAgentHeader.substring(slashIndex + 1);
  }

  /**
   * Todo.
   * @param siteIdList Todo.
   * @return Todo.
   */
  public static Integer[] convertStringListToIntegerArray(List<String> siteIdList) {
    Integer[] idArray = new Integer[siteIdList.size()];

    for (int i = 0; i < siteIdList.size(); i++) {
      String siteId = siteIdList.get(i);
      try {
        idArray[i] = Integer.parseInt(siteId);
      } catch (NumberFormatException e) {
        logger.error("Error converting String to Integer: " + siteId);
        idArray[i] = null;
      }
    }

    return idArray;
  }

  /**
   * Convert Date String from one format to another.
   *
   * @param dateString         the date string
   * @param sourceFormatString the format string of the date string to convert
   * @param targetFormatString the desired format string of the returned date string
   * @return the date in the desired target format
   */
  public static String convertDateStringToString(String dateString, String sourceFormatString,
      String targetFormatString) {
    SimpleDateFormat sourceFormat = new SimpleDateFormat(sourceFormatString);
    SimpleDateFormat targetFormat = new SimpleDateFormat(targetFormatString);

    try {
      return targetFormat.format(sourceFormat.parse(dateString));
    } catch (Exception e) {
      logger.warn("Could not convert date " + dateString + " from " + sourceFormatString + " to "
          + targetFormatString, e);
      return dateString;
    }
  }

  /**
   * Convert Date String from one format to another.
   *
   * @param dateString         the date string
   * @param sourceFormatString the format string of the date string to convert
   * @param targetFormatString the desired format string of the returned date string
   * @param sourceLocale       the locale of the source format
   * @param targetLocale       the desired locale for the returned date string
   * @return the date in the desired target format
   */
  public static String convertDateStringToString(String dateString, String sourceFormatString,
      String targetFormatString, Locale sourceLocale, Locale targetLocale) {

    try {

      Date date = convertStringToDate(dateString, sourceFormatString, sourceLocale);
      return convertDateToString(date, targetFormatString, targetLocale);

    } catch (ParseException e) {

      logger.warn("Could not convert date " + dateString + " from " + sourceFormatString + " to "
          + targetFormatString, e);
      return dateString;
    }
  }

  /**
   * Convert Date String from one format to another.
   *
   * @param dateString   the date string
   * @param sourceFormat the format of the date string to convert
   * @param targetFormat the desired format of the returned date string
   * @return the date in the desired target format
   */
  public static String convertDateStringToString(String dateString, DateFormat sourceFormat,
      DateFormat targetFormat) throws ParseException {
    return targetFormat.format(sourceFormat.parse(dateString));
  }

  /**
   * Todo.
   * @param dateString Todo.
   * @param sourceFormatString Todo.
   * @param sourceLocale Todo.
   * @return Todo.
   * @throws ParseException Todo.
   */
  public static Date convertStringToDate(String dateString, String sourceFormatString,
      Locale sourceLocale) throws ParseException {

    SimpleDateFormat sourceFormat = new SimpleDateFormat(sourceFormatString, sourceLocale);
    return sourceFormat.parse(dateString);

  }

  /**
   * Todo.
   * @param date Todo.
   * @param targetFormatString Todo.
   * @param targetLocale Todo.
   * @return Todo.
   */
  public static String convertDateToString(Date date, String targetFormatString,
      Locale targetLocale) {

    SimpleDateFormat targetFormat = new SimpleDateFormat(targetFormatString, targetLocale);
    return targetFormat.format(date);

  }

  /**
   * Convert an SQL Timestamp to the given target format.
   *
   * @param timestamp    the sql timestamp
   * @param targetFormat date format for the output string
   * @return the timestamp in the desired target format
   */
  public static String convertSqlTimestampToString(Timestamp timestamp, DateFormat targetFormat) {
    try {
      return targetFormat.format(timestamp);
    } catch (Exception e) {
      logger.warn("Could not convert SQL Timestamp to String.");
      return "";
    }
  }

  /**
   * Convert an SQL Timestamp to the given target format.
   *
   * @param timestamp          the sql timestamp
   * @param targetFormatString the desired format of the returned date string
   * @return the timestamp in the desired target format
   */
  public static String convertSqlTimestampToString(Timestamp timestamp, String targetFormatString) {
    SimpleDateFormat targetFormat = new SimpleDateFormat(targetFormatString);
    return convertSqlTimestampToString(timestamp, targetFormat);
  }

  /**
   * Convert a date in the given format to an SQL timestamp.
   *
   * @param timestampString the date as string
   * @param sourceFormat    the simple date format of the input timestamp string
   * @return the sql timestamp
   */
  public static Timestamp convertDateStringToSqlTimestamp(String timestampString,
      DateFormat sourceFormat) {
    try {
      Date date = sourceFormat.parse(timestampString);
      return new Timestamp(date.getTime());
    } catch (Exception e) {
      logger.warn("Could not convert SQL Timestamp to String.");
      return getCurrentSqlTimestamp();
    }
  }

  /**
   * Todo.
   * @param from Todo.
   * @param to Todo.
   * @return Todo.
   */
  public static int getMonthsDiff(java.sql.Date from, java.sql.Date to) {
    PeriodType months = PeriodType.months();
    Period difference = new Period(new LocalDate(from.getTime()), new LocalDate(to.getTime()),
        months);

    return difference.getMonths();
  }

  /**
   * Take a String and prefix it with exactly one slash.
   *
   * @param in the path as a string
   * @return the path prefixed with exactly one slash
   */
  public static String fixPath(String in) {
    if (SamplyShareUtils.isNullOrEmpty(in)) {
      return "";
    }
    return "/" + in.replaceAll("^/+", "");
  }

  /**
   * Take an URL and set the correct port and path.
   *
   * @param in the URL to fix
   * @return Todo.
   */
  public static URL fixUrl(URL in) throws MalformedURLException {
    int port = in.getPort();
    if (port < 0) {
      port = in.getDefaultPort();
    }
    String path = fixPath(in.getPath());
    URL ret = new URL(in.getProtocol(), in.getHost(), port, path);
    return ret;
  }

  /**
   * Get an URL from a String. Prefix with "http://" if no protocol is set.
   *
   * @param s String representation of the URL
   * @return the URL
   */
  public static URL stringToUrl(String s) throws MalformedURLException {
    if (s.startsWith("http")) {
      return fixUrl(new URL(s));
    } else {
      return fixUrl(new URL("http://" + s));
    }
  }

  /**
   * Gets the given string as http host.
   *
   * @param hostString the host string
   * @return the http host
   */
  public static HttpHost getAsHttpHost(String hostString) throws MalformedURLException {
    URL url = stringToUrl(hostString);
    // Get target as HttpHost
    return new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
  }

  /**
   * Gets the given url as http host.
   *
   * @param url the host url
   * @return the http host
   */
  public static HttpHost getAsHttpHost(URL url) {
    return new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
  }

  public static boolean isNullOrEmpty(final String s) {
    return s == null || s.isEmpty();
  }

  public static boolean isNullOrEmpty(final Collection<?> c) {
    return c == null || c.isEmpty();
  }

  public static boolean isNullOrEmpty(final Map<?, ?> m) {
    return m == null || m.isEmpty();
  }

  /**
   * Creates a random digit string.
   *
   * @param length the length of the digit string
   * @return the digit string
   */
  public static String createRandomDigitString(final int length) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < length; i++) {
      sb.append((int) (Math.random() * 10));
    }
    return sb.toString();
  }

  /**
   * Creates a random alphanumeric string.
   *
   * @param length the length of the string
   * @return the random string
   */
  public static String createRandomAlphanumericString(final int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(ALPHANUMERICS.charAt(SamplyShareUtils.random.nextInt(ALPHANUMERICS.length())));
    }
    return sb.toString();
  }

  /**
   * Unmarshal an object from xml.
   *
   * @param xml     the source xml string
   * @param context the jaxb context
   * @param clazz   the target class
   * @param <T>     the target type
   * @return the target object
   */
  public static <T> T unmarshal(String xml, JAXBContext context, Class<T> clazz)
      throws JAXBException {
    StringReader stringReader = new StringReader(xml);
    Object obj = context.createUnmarshaller().unmarshal(new StreamSource(stringReader));
    return (T) (obj instanceof JAXBElement ? ((JAXBElement) obj).getValue() : obj);
  }

  /**
   * Marshal an object to xml.
   *
   * @param object  the object to marshal
   * @param context the jaxb context, which has to be initalized with the correct class
   * @param <T>     the type of the object to marshal
   * @return the marshalled object
   */
  public static <T> String marshal(T object, JAXBContext context) {
    try {
      StringWriter stringWriter = new StringWriter();
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      marshaller.marshal(object, stringWriter);
      return stringWriter.toString();
    } catch (JAXBException e) {
      logger.error(String.format("Exception while marshalling", e));
      return "";
    }
  }

  /**
   * Encrypt.
   *
   * @param key       the key
   * @param plaintext the plaintext byte array
   * @return the encrypted byte array
   */
  public static byte[] encrypt(PublicKey key, byte[] plaintext) throws NoSuchAlgorithmException,
      NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
    Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(plaintext);
  }

  /**
   * Swap keys and values in a map.
   *
   * @param map Todo.
   * @param <K> Todo.
   * @param <V> Todo.
   * @return the map where keys and values are swapped. So [1,a], [2,b] would now be [a,1], [b,2]
   */
  public static <K, V> HashMap<V, K> reverse(Map<K, V> map) {
    HashMap<V, K> rev = new HashMap<V, K>();
    for (Map.Entry<K, V> entry : map.entrySet()) {
      rev.put(entry.getValue(), entry.getKey());
    }
    return rev;
  }

  /**
   * Convert a DataElement to a Result.
   *
   * @param dataElement the dataelement to convert
   * @return the result
   */
  public static Result dataElementToResult(DataElement dataElement) {
    Result result = new Result();
    try {
      result.setId(dataElement.getIdentification().getUrn());
      result.setIdentification(dataElement.getIdentification());
      result.setDesignations(dataElement.getDesignations());
      result.setType(EnumElementType.DATAELEMENT.name());
    } catch (Exception e) {
      logger.error("Error converting dataElement to Result: " + dataElement, e);
    }
    return result;
  }

  /**
   * Todo.
   * @param sdate Todo.
   * @param locale Todo.
   * @return Todo.
   */
  public static Date autodiscoverDate(String sdate, Locale locale) {

    Date date = null;

    if (isParseable(sdate)) {

      for (String format : AVAILABLE_FORMATS) {
        date = getDate(sdate, format, locale);
        if (date != null) {
          break;
        }
      }

    }

    return date;

  }

  private static boolean isParseable(String sdate) {

    boolean isParseable = false;

    for (String pattern : AVAILABLE_PATTERNS) {
      if (sdate.matches(pattern)) {
        isParseable = true;
        break;
      }
    }

    return isParseable;

  }

  private static Date getDate(String date, String format, Locale locale) {

    try {

      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, locale);
      return simpleDateFormat.parse(date);

    } catch (ParseException e) {
      return null;
    }

  }

}
