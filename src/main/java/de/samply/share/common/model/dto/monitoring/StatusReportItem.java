package de.samply.share.common.model.dto.monitoring;

import com.google.gson.annotations.SerializedName;

public class StatusReportItem {

  public static final String PARAMETER_LDM_STATUS = "bridgehead-ldm";
  public static final String PARAMETER_LDM_VERSION = "bridgehead-ldm-version";
  public static final String PARAMETER_IDM_STATUS = "bridgehead-idm";
  public static final String PARAMETER_IDM_VERSION = "bridgehead-idm-version";
  public static final String PARAMETER_SHARE_STATUS = "bridgehead-teiler";
  public static final String PARAMETER_SHARE_VERSION = "bridgehead-teiler-version";
  public static final String
      PARAMETER_REFERENCE_QUERY_RESULTCOUNT = "bridgehead-referencequery-resultcount";
  public static final String
      PARAMETER_REFERENCE_QUERY_RUNTIME = "bridgehead-referencequery-duration";
  public static final String PARAMETER_PATIENTS_TOTAL_COUNT = "bridgehead-patients-total";
  public static final String
      PARAMETER_PATIENTS_DKTKFLAGGED_COUNT = "bridgehead-patients-dktkflagged";
  public static final String PARAMETER_CENTRAXX_MAPPING_VERSION = "centraxx-mapping-version";
  public static final String PARAMETER_CENTRAXX_MAPPING_DATE = "centraxx-mapping-date";
  public static final String PARAMETER_JOB_CONFIG = "job-config";
  public static final String PARAMETER_INQUIRY_INFO = "inquiry-info";

  @SerializedName(value = "parameter_name")
  private String parameterName;
  @SerializedName(value = "status_text")
  private String statusText;
  @SerializedName(value = "exit_status")
  private String exitStatus;

  public String getParameterName() {
    return parameterName;
  }

  public void setParameterName(String parameterName) {
    this.parameterName = parameterName;
  }

  public String getStatusText() {
    return statusText;
  }

  public void setStatusText(String statusText) {
    this.statusText = statusText;
  }

  public String getExitStatus() {
    return exitStatus;
  }

  public void setExitStatus(String exitStatus) {
    this.exitStatus = exitStatus;
  }
}
