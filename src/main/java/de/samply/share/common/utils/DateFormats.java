package de.samply.share.common.utils;

import de.samply.web.enums.EnumDateFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateFormats {

  public DateFormats() {
  }

  /**
   * Todo.
   * @param enumDateFormat Todo.
   * @return Todo.
   */
  public static String getDatePattern(EnumDateFormat enumDateFormat) {
    Object formatter;
    switch (enumDateFormat) {
      case ISO_8601:
        formatter = new SimpleDateFormat("yyyy-MM");
        break;
      case ISO_8601_WITH_DAYS:
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        break;
      case DIN_5008:
        formatter = new SimpleDateFormat("MM.yyyy");
        break;
      case DIN_5008_WITH_DAYS:
        formatter = new SimpleDateFormat("dd.MM.yyyy");
        break;
      case DIN_5008_ONLY_YEAR:
        formatter = new SimpleDateFormat("yyyy");
        break;
      case LOCAL_DATE_WITH_DAYS:
      case LOCAL_DATE:
      default:
        formatter = DateFormat.getDateInstance(2, Locale.getDefault());
    }

    return ((SimpleDateFormat) formatter).toPattern();
  }

  public static String getDatepickerPattern(EnumDateFormat enumDateFormat) {
    String datePattern = getDatePattern(enumDateFormat);
    return datePattern.toUpperCase();
  }

}
