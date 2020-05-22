package org.folio.service;

import static org.folio.rest.tools.utils.TenantTool.calculateTenantId;

import java.util.Map;
import java.util.Optional;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.folio.model.User;
import org.folio.okapi.common.XOkapiHeaders;
import org.folio.rest.tools.client.HttpClientFactory;
import org.folio.rest.tools.client.Response;
import org.folio.rest.tools.client.interfaces.HttpClientInterface;
import org.folio.util.TokenUtils;
import org.folio.util.UserInfo;

@Component
public class UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

  private static final String USERS_ENDPOINT_TEMPLATE = "/users/%s";

  private static final String AUTHORIZATION_FAILURE_MESSAGE = "Authorization failure";
  private static final String USER_NOT_FOUND_MESSAGE = "User not found";
  private static final String CANNOT_GET_USER_DATA_MESSAGE = "Cannot get user data: %s";

  /**
   * Returns the user information for the userid specified in the x-okapi-token header.
   *
   * @param okapiHeaders The headers for the current API call.
   * @return User information based on userid from header.
   */
  public Future<User> getUserInfo(final Map<String, String> okapiHeaders) {
    CaseInsensitiveMap<String, String> headers = new CaseInsensitiveMap<>(okapiHeaders);

    String token = calculateTenantId(headers.get(XOkapiHeaders.TOKEN));
    Optional<UserInfo> userInfo = TokenUtils.userInfoFromToken(token);

    return userInfo.isPresent()
      ? fetchUser(userInfo.get(), headers)
      : failedPromise();
  }

  private Future<User> fetchUser(UserInfo userInfo, CaseInsensitiveMap<String, String> headers) {
    Promise<User> promise = Promise.promise();
    final String tenantId = calculateTenantId(headers.get(XOkapiHeaders.TENANT));
    String okapiURL = headers.get(XOkapiHeaders.URL);
    String userId = userInfo.getUserId();
    String url = String.format(USERS_ENDPOINT_TEMPLATE, userId);
    try {
      final HttpClientInterface httpClient = HttpClientFactory.getHttpClient(okapiURL, tenantId);
      httpClient.request(url, headers)
        .thenApply(response -> {
          try {
            if (Response.isSuccess(response.getCode())) {
              return response.getBody().mapTo(User.class);
            } else if (response.getCode() == 401 || response.getCode() == 403) {
              LOGGER.error(AUTHORIZATION_FAILURE_MESSAGE);
              throw new NotAuthorizedException(AUTHORIZATION_FAILURE_MESSAGE);
            } else if (response.getCode() == 404) {
              LOGGER.error(USER_NOT_FOUND_MESSAGE);
              throw new NotFoundException(USER_NOT_FOUND_MESSAGE);
            } else {
              String msg = String.format(CANNOT_GET_USER_DATA_MESSAGE, response.getError());
              LOGGER.error(msg, response.getException());
              throw new IllegalStateException(response.getError().toString());
            }
          } finally {
            httpClient.closeClient();
          }
        })
        .thenAccept(promise::complete)
        .exceptionally(e -> {
          promise.fail(e.getCause());
          return null;
        });
    } catch (Exception e) {
      String msg = String.format(CANNOT_GET_USER_DATA_MESSAGE, e.getMessage());
      LOGGER.error(msg, e);
      promise.fail(e);
    }

    return promise.future();
  }

  private Future<User> failedPromise() {
    Promise<User> promise = Promise.promise();
    LOGGER.error(AUTHORIZATION_FAILURE_MESSAGE);
    promise.fail(new NotAuthorizedException(AUTHORIZATION_FAILURE_MESSAGE));
    return promise.future();
  }
}
