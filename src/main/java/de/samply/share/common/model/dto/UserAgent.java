
package de.samply.share.common.model.dto;

/**
 * User agent which will be send from the share client to the searchbroker.
 */
public class UserAgent {

  private String projectContext;
  private String shareName;
  private String shareVersion;
  private String ldmName;
  private String ldmVersion;
  private String idManagerName;
  private String idManagerVersion;

  /**
   * Create user agent.
   *
   * @param projectContext   the project context
   * @param shareName        name of the Share Client
   * @param shareVersion     version of the Share Client
   * @param ldmName          name of the local data management
   * @param ldmVersion       version of the local data management
   * @param idManagerName    name of the IdManager
   * @param idManagerVersion version of IdManager
   */
  public UserAgent(String projectContext, String shareName, String shareVersion, String ldmName,
      String ldmVersion,
      String idManagerName, String idManagerVersion) {
    this.projectContext = projectContext;
    this.shareName = shareName;
    this.shareVersion = shareVersion;
    this.ldmName = ldmName;
    this.ldmVersion = ldmVersion;
    this.idManagerName = idManagerName;
    this.idManagerVersion = idManagerVersion;
  }

  /**
   * Create user agent.
   *
   * @param projectContext the project context
   * @param shareName      name of the Share Client
   * @param shareVersion   version of the Share Client
   */
  public UserAgent(String projectContext, String shareName, String shareVersion) {
    this.projectContext = projectContext;
    this.shareName = shareName;
    this.shareVersion = shareVersion;
  }

  /**
   * Convert and create a user agent from a string.
   *
   * @param userAgentString the user agent as string
   */
  public UserAgent(String userAgentString) {
    if (userAgentString == null) {
      this.projectContext = "unknown";
      this.shareName = "unknown";
      this.shareVersion = "unknown";
      return;
    }
    int ldmStart = userAgentString.indexOf("ldm=");
    int idmStart = userAgentString.indexOf("idm=");
    int ldmSlash = ldmStart > 0 ? userAgentString.indexOf('/', ldmStart) : -1;
    int idmSlash = idmStart > 0 ? userAgentString.indexOf('/', idmStart) : -1;
    int shareSlash = userAgentString.indexOf('/');

    if (shareSlash < 0) {
      this.projectContext = "unknown";
      this.shareName = "unknown";
      this.shareVersion = "unknown";
      return;
    }

    this.shareName = userAgentString.substring(0, shareSlash);
    int shareEnd = userAgentString.indexOf(' ', shareSlash);
    if (shareEnd > 0) {
      this.shareVersion = userAgentString.substring(shareSlash + 1, shareEnd);
    } else {
      this.shareVersion = userAgentString.substring(shareSlash + 1);
    }
    int projectBegin = userAgentString.indexOf('(');
    int projectEnd = userAgentString.indexOf(')', projectBegin);
    if (projectBegin > 0 && projectEnd > 0) {
      this.projectContext = userAgentString.substring(projectBegin + 1, projectEnd);
    }
    if (ldmStart > 0) {
      this.ldmName = userAgentString.substring(ldmStart + 4, ldmSlash);
      int ldmEnd = userAgentString.indexOf(' ', ldmSlash);
      if (ldmEnd > 0) {
        this.ldmVersion = userAgentString.substring(ldmSlash + 1, ldmEnd);
      } else {
        this.ldmVersion = userAgentString.substring(ldmSlash + 1);
      }
    }
    if (idmStart > 0) {
      this.idManagerName = userAgentString.substring(idmStart + 4, idmSlash);
      int idmEnd = userAgentString.indexOf(' ', idmSlash);
      if (idmEnd > 0) {
        this.idManagerVersion = userAgentString.substring(idmSlash + 1, idmEnd);
      } else {
        this.idManagerVersion = userAgentString.substring(idmSlash + 1);
      }
    }
  }

  /**
   * Get the project context.
   *
   * @return the projectContext
   */
  public String getProjectContext() {
    return projectContext;
  }

  /**
   * Set the project context.
   *
   * @param projectContext the projectContext to set
   */
  public void setProjectContext(String projectContext) {
    this.projectContext = projectContext;
  }

  /**
   * Get the Share Client name.
   *
   * @return the shareName
   */
  public String getShareName() {
    return shareName;
  }

  /**
   * Set the Share Client name.
   *
   * @param shareName the shareName to set
   */
  public void setShareName(String shareName) {
    this.shareName = shareName;
  }

  /**
   * Get the Share Client version.
   *
   * @return the shareVersion
   */
  public String getShareVersion() {
    return shareVersion;
  }

  /**
   * Set the Share Client version.
   *
   * @param shareVersion the shareVersion to set
   */
  public void setShareVersion(String shareVersion) {
    this.shareVersion = shareVersion;
  }

  /**
   * Get the local data management name.
   *
   * @return the ldmName
   */
  public String getLdmName() {
    return ldmName;
  }

  /**
   * Set the local data management name.
   *
   * @param ldmName the ldmName to set
   */
  public void setLdmName(String ldmName) {
    this.ldmName = ldmName;
  }

  /**
   * Get the local data management version.
   *
   * @return the ldmVersion
   */
  public String getLdmVersion() {
    return ldmVersion;
  }

  /**
   * Set the local data management version.
   *
   * @param ldmVersion the ldmVersion to set
   */
  public void setLdmVersion(String ldmVersion) {
    this.ldmVersion = ldmVersion;
  }

  /**
   * Get the IdManager name.
   *
   * @return the IdManager Name
   */
  public String getIdManagerName() {
    return idManagerName;
  }

  /**
   * Set the IdManager name.
   *
   * @param idManagerName the idManagerName to set
   */
  public void setIdManagerName(String idManagerName) {
    this.idManagerName = idManagerName;
  }

  /**
   * Get the IdManager.
   *
   * @return the idManagerVersion
   */
  public String getIdManagerVersion() {
    return idManagerVersion;
  }

  /**
   * Set the IdManager version.
   *
   * @param idManagerVersion the idManagerVersion to set
   */
  public void setIdManagerVersion(String idManagerVersion) {
    this.idManagerVersion = idManagerVersion;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (shareName != null) {
      builder.append(shareName);
    } else {
      builder.append("unknownClient");
    }
    builder.append("/");
    if (shareVersion != null) {
      builder.append(shareVersion);
    } else {
      builder.append("unknown");
    }

    if (projectContext != null) {
      builder.append(" (");
      builder.append(projectContext);
      builder.append(")");
    }

    if (ldmName != null || ldmVersion != null) {
      builder.append(" ldm=");
      if (ldmName != null) {
        builder.append(ldmName);
      } else {
        builder.append("unknown");
      }
      builder.append("/");
      if (ldmVersion != null) {
        builder.append(ldmVersion);
      } else {
        builder.append("unknown");
      }
    }

    if (idManagerName != null || idManagerVersion != null) {
      builder.append(" idm=");
      if (idManagerName != null) {
        builder.append(idManagerName);
      } else {
        builder.append("unknown");
      }
      builder.append("/");
      if (idManagerVersion != null) {
        builder.append(idManagerVersion);
      } else {
        builder.append("unknown");
      }
    }
    return builder.toString();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((idManagerName == null) ? 0 : idManagerName.hashCode());
    result = prime * result + ((idManagerVersion == null) ? 0 : idManagerVersion.hashCode());
    result = prime * result + ((ldmName == null) ? 0 : ldmName.hashCode());
    result = prime * result + ((ldmVersion == null) ? 0 : ldmVersion.hashCode());
    result = prime * result + ((projectContext == null) ? 0 : projectContext.hashCode());
    result = prime * result + ((shareName == null) ? 0 : shareName.hashCode());
    result = prime * result + ((shareVersion == null) ? 0 : shareVersion.hashCode());
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    UserAgent other = (UserAgent) obj;
    if (idManagerName == null) {
      if (other.idManagerName != null) {
        return false;
      }
    } else if (!idManagerName.equals(other.idManagerName)) {
      return false;
    }
    if (idManagerVersion == null) {
      if (other.idManagerVersion != null) {
        return false;
      }
    } else if (!idManagerVersion.equals(other.idManagerVersion)) {
      return false;
    }
    if (ldmName == null) {
      if (other.ldmName != null) {
        return false;
      }
    } else if (!ldmName.equals(other.ldmName)) {
      return false;
    }
    if (ldmVersion == null) {
      if (other.ldmVersion != null) {
        return false;
      }
    } else if (!ldmVersion.equals(other.ldmVersion)) {
      return false;
    }
    if (projectContext == null) {
      if (other.projectContext != null) {
        return false;
      }
    } else if (!projectContext.equals(other.projectContext)) {
      return false;
    }
    if (shareName == null) {
      if (other.shareName != null) {
        return false;
      }
    } else if (!shareName.equals(other.shareName)) {
      return false;
    }
    if (shareVersion == null) {
      if (other.shareVersion != null) {
        return false;
      }
    } else if (!shareVersion.equals(other.shareVersion)) {
      return false;
    }
    return true;
  }
}
