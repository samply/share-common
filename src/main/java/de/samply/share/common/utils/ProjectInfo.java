
package de.samply.share.common.utils;

import java.io.IOException;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * This class provides a single instance encapsulating information about the project. It is
 * initialized by the AbstractStartupListener
 */
public class ProjectInfo {

  public static final ProjectInfo INSTANCE = new ProjectInfo();

  /**
   * The project name.
   */
  private String projectName;

  /**
   * The version string.
   */
  private String versionString;

  /**
   * Build date string.
   */
  private String buildDateString;

  /**
   * The context.
   */
  private ServletContext context;

  /**
   * The configuration of the project.
   */
  private AbstractConfig config;

  /**
   * Gets the servlet context.
   *
   * @return the servlet context
   */
  public ServletContext getServletContext() {
    return context;
  }

  /**
   * Gets the project name.
   *
   * @return the project name
   */
  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  /**
   * Gets the version of the project as string.
   *
   * @return the version string
   */
  public String getVersionString() {
    return versionString;
  }

  public void setVersionString(String versionString) {
    this.versionString = versionString;
  }

  public void setContext(ServletContext context) {
    this.context = context;
  }

  public AbstractConfig getConfig() {
    return config;
  }

  public void setConfig(AbstractConfig config) {
    this.config = config;
  }

  public String getBuildDateString() {
    return buildDateString;
  }

  public void setBuildDateString(String buildDateString) {
    this.buildDateString = buildDateString;
  }

  /**
   * Reads project name and version from manifest file.
   *
   * @param sce the servlet context
   */
  public void initProjectMetadata(ServletContextEvent sce) {
    context = sce.getServletContext();
    Properties prop = new Properties();
    try {
      prop.load(context.getResourceAsStream("/META-INF/MANIFEST.MF"));
    } catch (IOException ex) {
      throw new RuntimeException("Manifest file not found", ex);
    }
    projectName = prop.getProperty("Samply-Project-Context");
    versionString = prop.getProperty("Implementation-Version");
    buildDateString = prop.getProperty("Build-Timestamp");
  }

}
