
package de.samply.share.common.utils;

import de.samply.config.util.FileFinderUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class AbstractConfig.
 */
public abstract class AbstractConfig {

  /**
   * The logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(AbstractConfig.class);
  /**
   * The props.
   */
  private Properties props;
  /**
   * The error msg.
   */
  private String errorMsg = null;
  /**
   * The config path.
   */
  private String configPath = null;
  /**
   * The file name of the config file.
   */
  private String configFilename;

  /**
   * Instantiates a new config.
   *
   * @param configFilename name of the config file
   */
  protected AbstractConfig(String configFilename, String... fallbacks) {
    this.configFilename = configFilename;
    props = new Properties();

    try {
      File file = FileFinderUtil
          .findFile(configFilename, ProjectInfo.INSTANCE.getProjectName(), fallbacks);

      logger.info("Reading config from file " + file.getAbsolutePath());

      FileInputStream configInputStream = new FileInputStream(file);

      props.load(configInputStream);
      configInputStream.close();
      logger.info("Config read successfully");

    } catch (IOException e) {
      errorMsg = "Error reading configuration file: " + e.getMessage();
      logger.error(errorMsg);
      throw new RuntimeException(errorMsg);
    }
  }

  /**
   * Gets the error msg.
   *
   * @return the error msg
   */
  public String getErrorMsg() {
    return errorMsg;
  }

  /**
   * Gets the properties.
   *
   * @return the properties
   */
  public Properties getProperties() {
    return props;
  }

  /**
   * Gets the property.
   *
   * @param propKey the prop key
   * @return the property
   */
  public String getProperty(String propKey) {
    return props.getProperty(propKey);
  }

  /**
   * Check whether debug is on.
   *
   * @return true, if successful
   */
  public boolean debugIsOn() {
    String debugMode = props.getProperty("debug");
    return (debugMode != null && debugMode.equalsIgnoreCase("true"));
  }

  protected void setProperty(String propKey, String val) {
    props.setProperty(propKey, val);
  }

  public String getConfigFileName() {
    return configFilename;
  }

  /**
   * Gets the config path.
   *
   * @return the config path
   */
  public String getConfigPath() {

    if (configPath == null) {
      try {
        configPath = FileFinderUtil.findFile(getConfigFileName(),
            ProjectInfo.INSTANCE.getProjectName(), null).getParent();
      } catch (FileNotFoundException e) {
        logger.error("Could not find config file...");
        e.printStackTrace();
      }
    }
    return configPath;
  }
}
