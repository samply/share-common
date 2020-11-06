package de.samply.share.common.utils.oauth2;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import de.samply.auth.client.jwt.JwtAccessToken;
import de.samply.auth.client.jwt.JwtException;
import de.samply.auth.client.jwt.KeyLoader;
import de.samply.auth.rest.AccessTokenDto;
import de.samply.auth.rest.AccessTokenRequestDto;
import de.samply.auth.rest.KeyIdentificationDto;
import de.samply.auth.rest.SignRequestDto;
import de.samply.share.common.utils.ProjectInfo;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc

/**
 * The Class OAuthUtils.
 */
public class OAuthUtils {

  /**
   * The Constant logger.
   */
  private static final Logger logger = LoggerFactory.getLogger(OAuthUtils.class);

  /**
   * Gets the access token.
   *
   * @param client     the client
   * @param authHost   the hostname of the oath server
   * @param keyId      the id of the key in auth
   * @param privateKey the private key for logging in in auth
   * @return the access token
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws InvalidKeyException      the invalid key exception
   * @throws SignatureException       the signature exception
   */
  public static AccessTokenDto getAccessToken(Client client, String authHost, String keyId,
      String privateKey)
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    KeyIdentificationDto identification = new KeyIdentificationDto();

    if (keyId == null || "".equalsIgnoreCase(keyId)) {
      logger.warn("keyId empty or null, returning null");
      return null;
    }

    identification.setKeyId(Integer.parseInt(keyId));
    ClientResponse response = client.resource(authHost + "/oauth2/signRequest")
        .accept("application/json").type("application/json")
        .post(ClientResponse.class, identification);
    if (response.getStatus() != 200) {
      logger.error(
          "Auth.getAccessToken returned " + response.getStatus() + " on signRequest bailing out!");
      return null;
    }
    SignRequestDto signRequest = response.getEntity(SignRequestDto.class);

    /**
     * Den Code signieren und in Base64 encodieren.
     */
    Signature sig = Signature.getInstance(signRequest.getAlgorithm());
    sig.initSign(KeyLoader.loadPrivateKey(privateKey));
    sig.update(signRequest.getCode().getBytes());
    String signature = Base64.encodeBase64String(sig.sign());

    AccessTokenRequestDto accessRequest = new AccessTokenRequestDto();
    accessRequest.setCode(signRequest.getCode());
    accessRequest.setSignature(signature);

    response = client.resource(authHost + "/oauth2/access_token").accept("application/json")
        .type("application/json").post(ClientResponse.class, accessRequest);

    if (response.getStatus() != 200) {
      logger.error("Auth.getAccessToken returned " + response.getStatus() + " bailing out!");
      return null;
    }

    AccessTokenDto accessToken = response.getEntity(AccessTokenDto.class);
    logger.trace("Access token: " + accessToken.getAccessToken());
    return accessToken;
  }

  /**
   * Get the {@link JwtAccessToken} from the authorisation header.
   *
   * @param authorizationHeader the authorisation header value
   *                            (e.g. "Bearer eyJhbGciOiJSUzUxMiJ(...)4R5fe3RTMYFuBuId8uzz6aswpI"
   * @return the access token from the authorisation header, or null if there is a issue with the
   *        authorisation header
   */
  public static JwtAccessToken getJwtAccessToken(final String authorizationHeader) {
    JwtAccessToken jwtAccesstoken = null;

    if (authorizationHeader != null) {
      String accessToken = authorizationHeader.replace("Bearer ", "");
      try {
        jwtAccesstoken = new JwtAccessToken(
            OAuthConfig.getOAuth2Client(ProjectInfo.INSTANCE.getProjectName()), accessToken);
      } catch (JwtException e) {
        logger.debug("Problems reading the access token: " + e);
      }
    }

    return jwtAccesstoken;
  }

  /**
   * Get the user authorisation ID from the authorisation header.
   *
   * @param authorizationHeader the authorisation header value
   *                            (e.g. "Bearer eyJhbGciOiJSUzUxMiJ(...)4R5fe3RTMYFuBuId8uzz6aswpI"
   * @return the user authorisation ID
   */
  public static String getUserAuthId(final String authorizationHeader) {
    String userAuthId = "";

    logger.debug("Getting user auth ID... ");

    JwtAccessToken jwtAccesstoken = getJwtAccessToken(authorizationHeader);
    if (jwtAccesstoken != null && jwtAccesstoken.isValid()) {
      userAuthId = jwtAccesstoken.getSubject();
    }

    logger.debug("User auth ID: " + userAuthId);

    return userAuthId;
  }
}
