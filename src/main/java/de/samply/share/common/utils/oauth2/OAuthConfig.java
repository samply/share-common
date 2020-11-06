package de.samply.share.common.utils.oauth2;

import de.samply.auth.rest.Scope;
import de.samply.auth.utils.OAuth2ClientConfig;
import de.samply.common.config.OAuth2Client;
import de.samply.common.config.ObjectFactory;
import de.samply.config.util.JaxbUtil;
import de.samply.share.common.utils.ProjectInfo;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class OAuthConfig {

  /**
   * The xhtml loaded on OAuth server redirect.
   */
  protected static final String SAMPLY_OAUTH_RESPONSE_XHTML = "/samplyLogin.xhtml";
  /**
   * The xhtml loaded on OAuth server redirect.
   */
  protected static final String LOCAL_LOGOUT_REDIRECT_XHTML = "/loginRedirect.xhtml";
  private static final String IP_DKFZ = "DKFZ";
  private static final String configFilename = "OAuth2Client.xml";
  private static final String DKFZ_LOGOUT_URI = "https://adfs1.dkfz.de/adfs/ls/?wa=wsignout1.0";
  private static final Logger logger = LoggerFactory.getLogger(OAuthConfig.class);

  /**
   * Get the url to be called for user authentication - from the authentication server. E.g. URL
   * called when the user clicks on a "Login with Samply account" button.
   *
   * @return the URL to be called for user authentication
   * @throws UnsupportedEncodingException Todo.
   */
  public static String getAuthLoginUrl() throws UnsupportedEncodingException {
    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

    String requestedPage = context.getRequestServletPath();
    if (requestedPage != null && requestedPage.startsWith("/") && requestedPage.length() > 1) {
      requestedPage = requestedPage.substring(1);
      if (requestedPage.endsWith(".xhtml") && requestedPage.length() > 6) {
        requestedPage = requestedPage.substring(0, requestedPage.length() - 6);
      }
    }
    Map<String, String> requestParameterMap = context.getRequestParameterMap();
    String inquiryId = requestParameterMap.get("inquiryId");
    String projectId = requestParameterMap.get("projectId");

    StringBuilder sb = new StringBuilder();
    if (requestedPage != null && requestedPage.length() > 0) {
      sb.append("?requestedPage=");
      sb.append(requestedPage);
      if (inquiryId != null) {
        sb.append("&inquiryId=");
        sb.append(inquiryId);
      } else if (projectId != null) {
        sb.append("&projectId=");
        sb.append(projectId);
      }
    }

    String projectName = ProjectInfo.INSTANCE.getProjectName();

    if (projectName.equalsIgnoreCase("dktk")) {
      return OAuth2ClientConfig.getRedirectUrl(getOAuth2Client(), context.getRequestScheme(),
          context.getRequestServerName(), context.getRequestServerPort(),
          context.getRequestContextPath(), SAMPLY_OAUTH_RESPONSE_XHTML + sb.toString(), IP_DKFZ,
          Scope.OPENID, Scope.MDR);
    } else {
      return OAuth2ClientConfig
          .getRedirectUrl(getOAuth2Client(projectName.toLowerCase()), context.getRequestScheme(),
              context.getRequestServerName(), context.getRequestServerPort(),
              context.getRequestContextPath(), SAMPLY_OAUTH_RESPONSE_XHTML + sb.toString(),
              Scope.OPENID, Scope.MDR);
    }
  }

  /**
   * Get the url to be called for user authentication - from the authentication server. E.g. URL
   * called when the user clicks on a "logout" button.
   *
   * @return the URL to be called for user log out in the authentication server
   * @throws UnsupportedEncodingException Todo.
   */
  public static String getAuthLogoutUrl() throws UnsupportedEncodingException {
    String projectName = ProjectInfo.INSTANCE.getProjectName();
    ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

    if (projectName.equalsIgnoreCase("dktk") && getOAuth2Client().getExternalIP() != null
        && getOAuth2Client().getExternalIP().equalsIgnoreCase("dkfz")) {
      String logoutUrl = getLogoutUrlDkfzAdfs(getOAuth2Client(), context.getRequestScheme(),
          context.getRequestServerName(), context.getRequestServerPort(),
          context.getRequestContextPath(), LOCAL_LOGOUT_REDIRECT_XHTML);

      logger.debug("LogoutURL: " + logoutUrl);
      return logoutUrl;
    } else {
      return OAuth2ClientConfig
          .getLogoutUrl(getOAuth2Client(projectName), context.getRequestScheme(),
              context.getRequestServerName(),
              context.getRequestServerPort(), context.getRequestContextPath(),
              LOCAL_LOGOUT_REDIRECT_XHTML);
    }
  }

  /**
   * Get the {@link OAuth2Client} instance.
   *
   * @return Todo.
   */
  public static OAuth2Client getOAuth2Client() {
    return getOAuth2Client("dktk");
  }

  /**
   * Todo.
   *
   * @param projectContext Todo.
   * @param fallbacks      Todo.
   * @return Todo.
   */
  public static OAuth2Client getOAuth2Client(String projectContext, String... fallbacks) {
    OAuth2Client oauthClient = null;
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
      oauthClient = JaxbUtil
          .findUnmarshall(configFilename, jaxbContext, OAuth2Client.class, projectContext,
              fallbacks);
    } catch (FileNotFoundException e) {
      logger.error(
          "Could not find config file: " + configFilename + " for context " + projectContext);
    } catch (JAXBException e) {
      logger.error("Could not read config file");
    } catch (SAXException e) {
      logger.error("Could not read config file");
    } catch (ParserConfigurationException e) {
      logger.error("Could not read config file");
    }
    return oauthClient;
  }

  /**
   * Todo.
   *
   * @param config           Todo.
   * @param scheme           Todo.
   * @param serverName       Todo.
   * @param port             Todo.
   * @param contextPath      Todo.
   * @param localRedirectUrl Todo.
   * @return Todo.
   * @throws UnsupportedEncodingException Todo.
   */
  public static String getLogoutUrlDkfzAdfs(OAuth2Client config, String scheme,
      String serverName, int port, String contextPath, String localRedirectUrl)
      throws UnsupportedEncodingException {

    String redirect = OAuth2ClientConfig.getLocalRedirectUrl(config, scheme,
        serverName, port,
        contextPath, localRedirectUrl);

    String host = OAuth2ClientConfig.getHost(config, serverName);

    StringBuilder builder = new StringBuilder(host);
    builder.append("/logout.xhtml?redirect_uri=")
        .append(URLEncoder.encode(DKFZ_LOGOUT_URI, StandardCharsets.UTF_8.displayName()))
        //            .append("&wreply=")
        //            .append(URLEncoder.encode(redirect, StandardCharsets.UTF_8.displayName()))
        .append("&client_id=")
        .append(URLEncoder.encode(config.getClientId(), StandardCharsets.UTF_8.displayName()));
    return builder.toString();
  }

}
