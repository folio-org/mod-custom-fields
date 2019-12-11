package org.folio.service;

import static org.folio.rest.RestVerticle.OKAPI_HEADER_TENANT;
import static org.folio.rest.RestVerticle.OKAPI_USERID_HEADER;
import static org.folio.rest.tools.utils.TenantTool.calculateTenantId;

import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.CaseInsensitiveHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import org.folio.model.User;
import org.folio.okapi.common.XOkapiHeaders;
import org.folio.rest.tools.client.HttpClientFactory;
import org.folio.rest.tools.client.Response;
import org.folio.rest.tools.client.interfaces.HttpClientInterface;

@Component
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);
  /**
   * Returns the user information for the userid specified in the original
   * request.
   *
   * @param okapiHeaders The headers for the current API call.
   * @return User information based on userid from header.
   */
  public Future<User> getUserInfo(final Map<String, String> okapiHeaders) {
    CaseInsensitiveHeaders headers = new CaseInsensitiveHeaders();
    headers.addAll(okapiHeaders);

    final String tenantId = calculateTenantId(headers.get(OKAPI_HEADER_TENANT));
    final String userId = headers.get(OKAPI_USERID_HEADER);
    Promise<User> promise = Promise.promise();
    if (userId == null) {
      logger.error("No userid header");
      promise.fail(new BadRequestException("Missing user id header, cannot look up user"));
      return promise.future();
    }

    String okapiURL = headers.get(XOkapiHeaders.URL);
    String url = "/users/" + userId;
    try {
      final HttpClientInterface httpClient = HttpClientFactory.getHttpClient(okapiURL, tenantId);
      httpClient.request(url, okapiHeaders)
        .thenApply(response -> {
          try {
            if (Response.isSuccess(response.getCode())) {
              return response.getBody().mapTo(User.class);
            } else if (response.getCode() == 401 || response.getCode() == 403) {
              logger.error("Authorization failure");
              throw new NotAuthorizedException("Authorization failure");
            } else if (response.getCode() == 404) {
              logger.error("User not found");
              throw new NotFoundException("User not found");
            } else {
              logger.error("Cannot get user data: " + response.getError().toString(), response.getException());
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
      logger.error("Cannot get user data: " + e.getMessage(), e);
      promise.fail(e);
    }

    return promise.future();
  }
}
