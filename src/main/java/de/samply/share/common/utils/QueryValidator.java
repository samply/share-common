package de.samply.share.common.utils;

import de.samply.common.mdrclient.MdrClient;
import de.samply.common.mdrclient.MdrConnectionException;
import de.samply.common.mdrclient.MdrInvalidResponseException;
import de.samply.common.mdrclient.domain.Slot;
import de.samply.common.mdrclient.domain.Validations;
import de.samply.share.common.model.uiquerybuilder.EnumOperator;
import de.samply.share.common.model.uiquerybuilder.QueryItem;
import de.samply.web.enums.EnumDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.omnifaces.model.tree.TreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple (and limited) Query Validator.
 * For now, only checks if a query contains boolean dataelements that have "t" or "f" as value.
 * Currently, this is just needed for incoming queries from central search.
 * Possible future modification: Remove redundancies from queries.
 */
public class QueryValidator {

  private static final Logger logger = LoggerFactory.getLogger(QueryValidator.class);

  // Language Code doesn't matter for the validations, so just hardcode it
  private static final String languageCode = "de";

  private static final String DATATYPE_BOOLEAN = "BOOLEAN";
  private static final String DATATYPE_DATE = "DATE";

  private static final String SLOTNAME_JAVA_DATE_FORMAT = "JAVA_DATE_FORMAT";
  private static final String DEFAULT_DATE_FORMAT = "dd.MM.yyyy";


  private MdrClient mdrClient;

  public QueryValidator(MdrClient mdrClient) {
    this.mdrClient = mdrClient;
  }

  /**
   * Fix boolean values in a query tree object.
   * May also be just a partial tree.
   *
   * @param node the root node from which to check for booleans to fix
   */
  public void fixBooleans(TreeModel<QueryItem> node) {
    if (node.isLeaf()) {
      String mdrId = node.getData().getMdrId();
      String value = node.getData().getValue();
      if (mdrId != null && mdrId.contains(":dataelement:")) {
        try {
          String fixedValue = getFixedBooleanValue(mdrId, value);
          if (!fixedValue.equals(value)) {
            node.getData().setValue(fixedValue);
          }
        } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
          logger.error("exception caught.", e);
        }
      }
    } else {
      for (TreeModel<QueryItem> child : node.getChildren()) {
        fixBooleans(child);
      }
    }
  }

  /**
   * If the dataelement has the validation type "boolean", set all "t" and "true" to true (not
   * case-sensitive).
   *
   * @param mdrKey the mdr id of the element to fix
   * @param value  the value to fix
   * @return true if the value was t or true, false otherwise
   */
  private String getFixedBooleanValue(String mdrKey, String value)
      throws MdrConnectionException, MdrInvalidResponseException, ExecutionException {
    Validations validations = mdrClient.getDataElementValidations(mdrKey, languageCode);

    if (validations.getDatatype().equalsIgnoreCase(DATATYPE_BOOLEAN)) {
      if (value.equalsIgnoreCase("t") || value.equalsIgnoreCase("true")) {
        return Boolean.TRUE.toString();
      } else {
        return Boolean.FALSE.toString();
      }
    }
    return value;
  }

  /**
   * Reformat all date entries in a query tree to the date format given in the java date format
   * slot.
   *
   * @param node the root-node from which to start
   */
  public void reformatDateToSlotFormat(TreeModel<QueryItem> node) {
    if (node.isLeaf() && node.getData() != null) {
      MdrIdDatatype mdrIdDatatype;
      try {
        mdrIdDatatype = new MdrIdDatatype(node.getData().getMdrId());
      } catch (Exception e) {
        mdrIdDatatype = null;
      }

      if (mdrIdDatatype != null && mdrIdDatatype.getDatatype().equalsIgnoreCase("dataelement")) {
        if (node.getData().getOperator().equals(EnumOperator.IS_NOT_NULL) || node.getData()
            .getOperator().equals(EnumOperator.IS_NULL)) {
          return;
        }
        String value = node.getData().getValue();
        if (node.getData().getOperator().equals(EnumOperator.BETWEEN)) {
          String lowerBound = node.getData().getLowerBound();
          String upperBound = node.getData().getUpperBound();
          try {
            String fixedLowerBound = getDateValueInSlotFormat(mdrIdDatatype.getLatestMdr(),
                lowerBound);
            if (!fixedLowerBound.equals(lowerBound)) {
              node.getData().setLowerBound(fixedLowerBound);
            }

            String fixedUpperBound = getDateValueInSlotFormat(mdrIdDatatype.getLatestMdr(),
                upperBound);
            if (!fixedUpperBound.equals(upperBound)) {
              node.getData().setUpperBound(fixedUpperBound);
            }
          } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
            logger.error("exception caught.", e);
          }
        } else {
          try {
            String fixedValue = getDateValueInSlotFormat(mdrIdDatatype.getLatestMdr(), value);
            if (!fixedValue.equals(value)) {
              node.getData().setValue(fixedValue);
            }
          } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
            logger.error("exception caught.", e);
          }
        }
      }
    } else {
      for (TreeModel<QueryItem> child : node.getChildren()) {
        reformatDateToSlotFormat(child);
      }
    }
  }

  /**
   * Reformat all date entries in a query tree from the date format given in the java date format
   * slot to the default format.
   * This might be necessary when values are submitted from central search to decentral search.
   *
   * @param node the root-node from which to start
   */
  public void reformatDateFromSlotFormat(TreeModel<QueryItem> node) {
    if (node.isLeaf() && node.getData() != null) {
      MdrIdDatatype mdrIdDatatype;
      try {
        mdrIdDatatype = new MdrIdDatatype(node.getData().getMdrId());
      } catch (Exception e) {
        mdrIdDatatype = null;
      }
      String value = node.getData().getValue();
      if (mdrIdDatatype != null && mdrIdDatatype.getDatatype().equalsIgnoreCase("dataelement")) {
        if (node.getData().getOperator().equals(EnumOperator.BETWEEN)) {
          String lowerBound = node.getData().getLowerBound();
          String upperBound = node.getData().getUpperBound();

          String fixedLowerBound = getDateValueInDefaultFormat(mdrIdDatatype.getLatestMdr(),
              lowerBound);
          if (!fixedLowerBound.equals(lowerBound)) {
            node.getData().setLowerBound(fixedLowerBound);
          }

          logger.error("upperbound is " + upperBound);
          String fixedUpperBound = getDateValueInDefaultFormat(mdrIdDatatype.getLatestMdr(),
              upperBound, true);
          logger.error("fixed upperbound is " + fixedUpperBound);
          if (!fixedUpperBound.equals(upperBound)) {
            node.getData().setUpperBound(fixedUpperBound);
          }

        } else {
          String fixedValue = getDateValueInDefaultFormat(mdrIdDatatype.getLatestMdr(), value);
          if (!fixedValue.equals(value)) {
            node.getData().setValue(fixedValue);
          }
        }
      }
    } else {
      for (TreeModel<QueryItem> child : node.getChildren()) {
        reformatDateFromSlotFormat(child);
      }
    }
  }

  /**
   * Convert a date string from the default format to the format specified in the date format slot.
   *
   * @param mdrKey mdr id of the dataelement
   * @param value  the value to reformat
   * @return a string representation of the given date in the slot date format or the unchanged
   *        value if the date format slot is not set
   */
  public String getDateValueInSlotFormat(String mdrKey, String value)
      throws MdrConnectionException, MdrInvalidResponseException, ExecutionException {
    Validations validations = mdrClient.getDataElementValidations(mdrKey, languageCode);
    ArrayList<Slot> slots = mdrClient.getDataElementSlots(mdrKey);

    if (validations.getDatatype().equalsIgnoreCase(DATATYPE_DATE)) {
      for (Slot slot : slots) {
        if (slot.getSlotName().equalsIgnoreCase(SLOTNAME_JAVA_DATE_FORMAT)) {
          String targetDateFormat = slot.getSlotValue();
          if (!targetDateFormat.equalsIgnoreCase(DEFAULT_DATE_FORMAT)) {
            try {
              return SamplyShareUtils
                  .convertDateStringToString(value, DEFAULT_DATE_FORMAT, targetDateFormat,
                      Locale.ENGLISH, Locale.ENGLISH);
            } catch (Exception e) {
              logger.error("exception caught...returning value without change.", e);
            }
          }
          break;
        }
      }
    }
    return value;
  }

  /**
   * Convert a date string from the default format to the format specified in the date format slot.
   *
   * @param mdrKey mdr id of the dataelement
   * @param value  the value to reformat
   * @return a string representation of the given date in the slot date format or the unchanged
   *        value if the date format slot is not set
   */
  public String getDateValueInElementValidationFormatWithSourceFormatAutodiscovering(String mdrKey,
      String value) throws MdrConnectionException, MdrInvalidResponseException, ExecutionException {

    Validations validations = mdrClient.getDataElementValidations(mdrKey, languageCode);

    if (validations.getDatatype().equalsIgnoreCase(DATATYPE_DATE)) {

      EnumDateFormat enumDateFormat = EnumDateFormat.valueOf(validations.getValidationData());
      String targetDateFormat = DateFormats.getDatePattern(enumDateFormat);

      try {

        Date date = SamplyShareUtils.autodiscoverDate(value, Locale.ENGLISH);
        if (date != null) {
          String resultDate = SamplyShareUtils
              .convertDateToString(date, targetDateFormat, Locale.ENGLISH);
          if (resultDate != null) {
            value = resultDate;
          }
        }

      } catch (Exception e) {
        logger.error("exception caught...returning value without change.", e);
      }

    }

    return value;

  }

  /**
   * "Expand" a date String from the Slot format to the default format. Day and Month will default
   * to "1" if not set in the source value.
   *
   * @param mdrKey mdr id of the dataelement
   * @param value  the value to reformat
   * @return a string representation of the given date in the default date format or the unchanged
   *        value if the date format slot is not set
   */
  public String getDateValueInDefaultFormat(String mdrKey, String value) {
    return getDateValueInDefaultFormat(mdrKey, value, false);
  }


  /**
   * "Expand" a date String from the Slot format to the default format. Day and Month will default
   * to either "1" or the maximum possible value if not set in the source value.
   *
   * @param mdrKey                      mdr id of the dataelement
   * @param value                       the value to reformat
   * @param setMissingToMaxInsteadOfMin if true, set missing month to 12 instead of 1 and missing
   *                                    day to 28-31 (depending on the month) instead of 1
   * @return a string representation of the given date in the default date format or the unchanged
   *        value if the date format slot is not set
   */
  public String getDateValueInDefaultFormat(String mdrKey, String value,
      boolean setMissingToMaxInsteadOfMin) {
    try {
      String slotDateFormat = getJavaDateFormatSlot(mdrKey);
      if (slotDateFormat == null) {
        logger.debug("No Java date format slot found, returning unmodified value.");
        return value;
      }
      if (!slotDateFormat.equalsIgnoreCase(DEFAULT_DATE_FORMAT)) {
        logger.error("slot format is " + slotDateFormat);
        try {
          if (setMissingToMaxInsteadOfMin) {
            logger.error("Set to max!");
            DateTime dt = DateTime.parse(value, DateTimeFormat.forPattern(slotDateFormat));
            if (slotDateFormat.equalsIgnoreCase("yyyy")) {
              logger.error("just year");
              return dt.withDayOfMonth(31).withMonthOfYear(12)
                  .toString(DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT));
            } else if (slotDateFormat.contains("MM")) {
              logger.error("just month and year");
              return dt.withDayOfMonth(31).toString(DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT));
            } else {
              logger.error("complete");
              return dt.toString(DateTimeFormat.forPattern(DEFAULT_DATE_FORMAT));
            }
          } else {
            logger.error("Set to min!");
            return SamplyShareUtils
                .convertDateStringToString(value, slotDateFormat, DEFAULT_DATE_FORMAT,
                    Locale.ENGLISH, Locale.ENGLISH);
          }
        } catch (Exception e) {
          logger.error("exception caught...returning unmodified value", e);
        }
      }
    } catch (MdrConnectionException | MdrInvalidResponseException | ExecutionException e) {
      logger.debug("Could not get slot value from mdr, returning unmodified value.");
    }
    return value;
  }

  /**
   * Get the value for the java date format slot.
   *
   * @param mdrKey the mdr id of the element to check
   * @return the java date format string or null if none is set
   */
  private String getJavaDateFormatSlot(String mdrKey)
      throws MdrConnectionException, MdrInvalidResponseException, ExecutionException {
    Validations validations = mdrClient.getDataElementValidations(mdrKey, languageCode);
    ArrayList<Slot> slots = mdrClient.getDataElementSlots(mdrKey);

    if (validations.getDatatype().equalsIgnoreCase(DATATYPE_DATE)) {
      for (Slot slot : slots) {
        if (slot.getSlotName().equalsIgnoreCase(SLOTNAME_JAVA_DATE_FORMAT)) {
          return slot.getSlotValue();
        }
      }
    }
    return null;
  }
}
